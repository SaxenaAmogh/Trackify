package com.example.trackify.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class TransactionViewModel: ViewModel() {

    data class FriendContribution(
        val name: String,
        val contribution: Float,
        val paid: Boolean
    )

    var expense by mutableStateOf(true)
    var title by mutableStateOf("")
    var amount by mutableStateOf("")
    var transactionType by mutableStateOf("Payment")
    var selectedFriends = mutableStateListOf<FriendContribution>()
    var date by mutableStateOf("")
    var category by mutableStateOf("Category")
    var note by mutableStateOf("")
    var MyContribution by mutableFloatStateOf(0f)

    fun addFriendContribution(name: String, contribution: Float, paid: Boolean) {
        selectedFriends.add(FriendContribution(name, contribution, paid))
    }

    fun clearTransaction() {
        title = ""
        amount = ""
        transactionType = "Payment"
        selectedFriends.clear()
        date = ""
        category = "Category"
        note = ""
    }

    fun clearSelectedFriends() {
        selectedFriends.clear()
    }

    private fun calcMyContribution(): Float {
        if (selectedFriends.size == 0){
            return amount.toFloat()
        }else{
            var total = 0f
            for (i in 0 until selectedFriends.size) {
                if (selectedFriends[i].paid){
                    total += selectedFriends[i].contribution
                }
            }
            MyContribution = amount.toFloat() - total
            return (amount.toFloat() - total)
        }
    }


    // Add this state at the top of your ViewModel
    private val _expenseStatus = mutableStateOf<String?>(null)
    val expenseStatus: State<String?> = _expenseStatus

    private val _incomeStatus = mutableStateOf<String?>(null)
    val incomeStatus: State<String?> = _incomeStatus

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun clearExpenseStatus() {
        _expenseStatus.value = null
    }

    fun clearIncomeStatus() {
        _incomeStatus.value = null
    }

    fun addExpense(
        title: String,
        amount: Double,
        paymentType: String,
        date: String,
        expenseType: String,
        friends: SnapshotStateList<FriendContribution>,
        note: String
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _expenseStatus.value = "User not logged in"
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val expenseData = mapOf(
            "Title" to title,
            "Amount" to amount,
            "PaymentType" to paymentType,
            "Date" to date,
            "ExpenseType" to expenseType,
            "Friends" to friends,
            "MyContribution" to calcMyContribution(),
            "Note" to note
        )

        val expenseDocRef = firestore.collection(uid).document("ExpenseDetails")

        // First get existing data to determine next Expense number
        expenseDocRef.get().addOnSuccessListener { document ->
            val existingCount = document.data?.size ?: 0
            val nextExpenseId = "Expense${existingCount + 1}"

            val updateMap = mapOf(nextExpenseId to expenseData)

            expenseDocRef.set(updateMap, SetOptions.merge())
                .addOnSuccessListener {
                    _expenseStatus.value = "success"
                }
                .addOnFailureListener { e ->
                    _expenseStatus.value = "Firestore error: ${e.message}"
                }
        }.addOnFailureListener { e ->
            _expenseStatus.value = "Failed to fetch expense doc: ${e.message}"
        }
    }

    fun addIncome(
        title: String,
        amount: Double,
        paymentType: String,
        date: String,
        note: String
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _incomeStatus.value = "User not logged in"
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val incomeData = mapOf(
            "Title" to title,
            "Amount" to amount,
            "PaymentType" to paymentType,
            "Date" to date,
            "Note" to note
        )

        val incomeDocRef = firestore.collection(uid).document("IncomeDetails")

        // First get existing data to determine next Income number
        incomeDocRef.get().addOnSuccessListener { document ->
            val existingCount = document.data?.size ?: 0
            val nextExpenseId = "Income${existingCount + 1}"

            val updateMap = mapOf(nextExpenseId to incomeData)

            incomeDocRef.set(updateMap, SetOptions.merge())
                .addOnSuccessListener {
                    _incomeStatus.value = "success"
                }
                .addOnFailureListener { e ->
                    _incomeStatus.value = "Firestore error: ${e.message}"
                }
        }.addOnFailureListener { e ->
            _incomeStatus.value = "Failed to fetch expense doc: ${e.message}"
        }
    }

    fun updateFriendContributions(expCategory: String, myContri: Float) {
        val uid = auth.currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()
        val friendDetailsRef = firestore.collection(uid).document("FriendDetails")

        val allCategories = listOf("Food", "Shopping", "Subscription", "Entertainment", "Travel", "Other")

        friendDetailsRef.get()
            .addOnSuccessListener { document ->
                val existingData = document.data ?: emptyMap<String, Any>()
                val updatedData = existingData.toMutableMap()

                for (friend in selectedFriends) {
                    val friendName = friend.name
                    val existingFriendMap = updatedData[friendName] as? Map<*, *>
                    var num = 0f

                    val updatedFriendData = if (existingFriendMap != null) {
                        val oldPending = (existingFriendMap["Pending"] as? Number)?.toFloat() ?: 0f
                        val oldMySpending = (existingFriendMap["MySpending"] as? Number)?.toFloat() ?: 0f
                        val oldCategoryMap = (existingFriendMap["Category"] as? Map<*, *>) ?: emptyMap<String, Any>()

                        val newCategoryMap = oldCategoryMap.toMutableMap().mapValues { (_, v) ->
                            (v as? Number)?.toFloat() ?: 0f
                        }.toMutableMap()

                        newCategoryMap[expCategory] = (newCategoryMap[expCategory] ?: 0f) + 1

                        num = if (!friend.paid){
                            oldPending + friend.contribution
                        }else{
                            oldPending
                        }

                        mapOf(
                            "Pending" to num,
                            "MySpending" to oldMySpending + myContri,
                            "Category" to newCategoryMap
                        )
                    } else {
                        val initialCategoryMap = allCategories.associateWith { 0f }.toMutableMap()
                        initialCategoryMap[expCategory] = 1f

                        num = if (!friend.paid){
                            friend.contribution
                        }else{
                            0f
                        }
                        mapOf(
                            "Pending" to num,
                            "MySpending" to myContri,
                            "Category" to initialCategoryMap
                        )
                    }

                    updatedData[friendName] = updatedFriendData
                }

                friendDetailsRef.set(updatedData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "FriendDetails updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error updating FriendDetails: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching FriendDetails: ${e.message}")
            }
    }




}