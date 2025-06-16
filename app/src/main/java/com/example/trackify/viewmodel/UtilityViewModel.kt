package com.example.trackify.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.trackify.model.Expense
import com.example.trackify.model.FriendEntry
import com.example.trackify.model.IncomeData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UtilityViewModel: ViewModel()  {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _netBalanceText = MutableStateFlow<Double?>(null)
    val netBalanceText: StateFlow<Double?> = _netBalanceText
    private var netBalanceListener: ListenerRegistration? = null

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses
    private var expenseListener: ListenerRegistration? = null

    // For raw all-expenses fetch (different from _expenses used elsewhere)
    private val _allExpensesRaw = MutableStateFlow<List<Expense>>(emptyList())
    val allExpensesRaw: StateFlow<List<Expense>> = _allExpensesRaw

    // For all income fetch
    private val _allIncomes = MutableStateFlow<List<IncomeData>>(emptyList())
    val allIncomes: StateFlow<List<IncomeData>> = _allIncomes

    private val _last4Expenses = MutableStateFlow<List<Expense>>(emptyList())
    val last4Expenses: StateFlow<List<Expense>> = _last4Expenses


    fun addToNetBalance(amount: Double, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(false, "User not logged in")
            return
        }

        val userDetailsRef = firestore.collection(uid).document("UserDetails")

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDetailsRef)
            val currentBalance = snapshot.getDouble("Net Balance") ?: 0.0
            val updatedBalance = currentBalance + amount
            transaction.update(userDetailsRef, "Net Balance", updatedBalance)
        }.addOnSuccessListener {
            onResult(true, null)
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }

    fun subtractFromNetBalance(amount: Double, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(false, "User not logged in")
            return
        }

        val userDetailsRef = firestore.collection(uid).document("UserDetails")

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDetailsRef)
            val currentBalance = snapshot.getDouble("Net Balance") ?: 0.0
            val updatedBalance = currentBalance - amount
            transaction.update(userDetailsRef, "Net Balance", updatedBalance)
        }.addOnSuccessListener {
            onResult(true, null)
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }

    fun startListeningToNetBalance() {
        val uid = auth.currentUser?.uid ?: return

        val userDetailsRef = firestore.collection(uid).document("UserDetails")

        // Remove existing listener if already running
        netBalanceListener?.remove()

        netBalanceListener = userDetailsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                _netBalanceText.value = 0.0
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val balance = snapshot.getDouble("Net Balance")
                _netBalanceText.value = balance
            } else {
                _netBalanceText.value = 0.0
            }
        }
    }

    fun startListeningToExpenses() {
        val uid = auth.currentUser?.uid ?: return
        val expenseRef = firestore.collection(uid).document("ExpenseDetails")

        // Remove old listener if needed
        expenseListener?.remove()

        expenseListener = expenseRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Expenses", "Listener failed: ${error.message}")
                return@addSnapshotListener
            }

            val allExpenses = mutableListOf<Pair<Int, Expense>>()  // Pair of index and expense

            snapshot?.data?.forEach { (key, value) ->
                val match = Regex("Expense(\\d+)").find(key)
                val number = match?.groupValues?.get(1)?.toIntOrNull()

                if (value is Map<*, *> && number != null) {
                    val expense = Expense(
                        Title = value["Title"] as? String ?: "",
                        Amount = (value["Amount"] as? Number)?.toDouble() ?: 0.0,
                        PaymentType = value["PaymentType"] as? String ?: "",
                        Date = value["Date"] as? String,
                        ExpenseType = value["ExpenseType"] as? String ?: "",
                        MyContribution = (value["MyContribution"] as? Number)?.toDouble() ?: 0.0,
                        Note = value["Note"] as? String ?: ""
                    )
                    allExpenses.add(number to expense)
                }
            }

            // Sort by number descending and take top 4
            val latest = allExpenses
                .sortedByDescending { it.first }
                .take(4)
                .map { it.second }

            _last4Expenses.value = latest
        }
    }

    fun fetchAllRawExpenses() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection(uid)
            .document("ExpenseDetails")
            .get()
            .addOnSuccessListener { document ->
                val expensesList = document.data?.mapNotNull { (_, value) ->
                    (value as? Map<*, *>)?.let { itMap ->
                        val friendsList = (itMap["Friends"] as? List<*>)?.mapNotNull { friend ->
                            (friend as? Map<*, *>)?.let { f ->
                                FriendEntry(
                                    name = f["name"] as? String ?: "",
                                    contribution = (f["contribution"] as? Number)?.toDouble() ?: 0.0,
                                    paid = f["paid"] as? Boolean ?: false
                                )
                            }
                        } ?: emptyList()

                        Expense(
                            Title = itMap["Title"] as? String ?: "",
                            Amount = (itMap["Amount"] as? Number)?.toDouble() ?: 0.0,
                            PaymentType = itMap["PaymentType"] as? String ?: "",
                            Date = itMap["Date"] as? String ?: "",
                            ExpenseType = itMap["ExpenseType"] as? String ?: "",
                            Friends = friendsList,
                            MyContribution = (itMap["MyContribution"] as? Number)?.toDouble() ?: 0.0,
                            Note = itMap["Note"] as? String ?: ""
                        )
                    }
                } ?: emptyList()


                _allExpensesRaw.value = expensesList.reversed()
            }
    }

    fun fetchAllIncomes() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection(uid)
            .document("IncomeDetails")
            .get()
            .addOnSuccessListener { document ->
                val incomeList = document.data?.mapNotNull { (_, value) ->
                    (value as? Map<*, *>)?.let {
                        IncomeData(
                            Title = it["Title"] as? String ?: "",
                            Amount = (it["Amount"] as? Number)?.toDouble() ?: 0.0,
                            PaymentType = it["PaymentType"] as? String ?: "",
                            Date = it["Date"] as? String ?: "",
                            Note = it["Note"] as? String ?: ""
                        )
                    }
                } ?: emptyList()

                _allIncomes.value = incomeList.reversed()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMonthExpenses(expenses: List<Expense>, formatter: DateTimeFormatter): Double {
        val today = LocalDate.now()
        val currentMonth = today.monthValue
        val currentYear = today.year

        return expenses
            .filter {
                val expenseDate = LocalDate.parse(it.Date, formatter)
                expenseDate.monthValue == currentMonth && expenseDate.year == currentYear
            }
            .sumOf { it.MyContribution ?: 0.0 }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMonthIncomes(income: List<IncomeData>, formatter: DateTimeFormatter): Double {
        val today = LocalDate.now()
        val currentMonth = today.monthValue
        val currentYear = today.year

        return income
            .filter {
                val incomeDate = LocalDate.parse(it.Date, formatter)
                incomeDate.monthValue == currentMonth && incomeDate.year == currentYear
            }
            .sumOf { it.Amount ?: 0.0 }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMonthExpensesFriends(expenses: List<Expense>, formatter: DateTimeFormatter): Double {
        val today = LocalDate.now()
        val currentMonth = today.monthValue
        val currentYear = today.year

        return expenses
            .filter {
                val expenseDate = LocalDate.parse(it.Date, formatter)
                expenseDate.monthValue == currentMonth && expenseDate.year == currentYear
            }
            .sumOf {
                if (it.Friends.isNotEmpty()){
                    it.MyContribution ?: 0.0
                }else{0.0}
            }
    }

    override fun onCleared() {
        super.onCleared()
        netBalanceListener?.remove()
        expenseListener?.remove()
    }
}