package com.example.trackify.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.trackify.model.Expense
import com.example.trackify.model.IncomeData
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ReportsViewModel: ViewModel() {

    data class WeeklyTotals(
        val week: String,
        val income: Float,
        val expense: Float
    )

    data class CategoryCount(
        val category: String,
        val count: Int
    )

    data class CategoryStats(
        val counts: List<CategoryCount>,
        val amounts: Map<String, Double>
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFilteredIncomeAndExpenseTotal(
        filter: String,
        expenses: List<Expense>,
        incomes: List<IncomeData>
    ): Pair<Double, Double> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // change if using another format
        val today = LocalDate.now()

        fun isInRange(date: LocalDate?): Boolean {
            if (date == null) return false

            return when (filter) {
                "This Month" -> date.month == today.month && date.year == today.year
                "Last Month" -> {
                    val lastMonth = today.minusMonths(1)
                    date.month == lastMonth.month && date.year == lastMonth.year
                }
                "This Week" -> {
                    val startOfWeek = today.with(DayOfWeek.MONDAY)
                    val endOfWeek = today.with(DayOfWeek.SUNDAY)
                    !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
                }
                "This Year" -> date.year == today.year
                else -> true
            }
        }

        val totalExpense = expenses
            .mapNotNull { runCatching { LocalDate.parse(it.Date, formatter) to it.MyContribution }.getOrNull() }
            .filter { (date, _) -> isInRange(date) }
            .sumOf { (_, amount) -> amount }

        val totalIncome = incomes
            .mapNotNull { runCatching { LocalDate.parse(it.Date, formatter) to it.Amount }.getOrNull() }
            .filter { (date, _) -> isInRange(date) }
            .sumOf { (_, amount) -> amount }

        return Pair(totalIncome, totalExpense)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateWeeklyTotalsThisMonth(
        incomes: List<IncomeData>,
        expenses: List<Expense>
    ): List<WeeklyTotals> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // change if using another format
        val now = LocalDate.now()
        val currentMonth = now.monthValue
        val currentYear = now.year

        val incomePerWeek = FloatArray(5) { 0f }
        val expensePerWeek = FloatArray(5) { 0f }

        fun getWeekOfMonth(date: LocalDate): Int {
            val firstDayOfMonth = date.withDayOfMonth(1)
            val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // Mon=1..Sun=7
            return ((date.dayOfMonth + dayOfWeekOffset - 1) / 7).coerceAtMost(4)
        }

        incomes.forEach {
            try {
                val date = LocalDate.parse(it.Date, formatter)
                if (date.monthValue == currentMonth && date.year == currentYear) {
                    val week = getWeekOfMonth(date)
                    incomePerWeek[week] += it.Amount.toFloat()
                }
            } catch (_: Exception) { }
        }
        Log.e(
            "ReportsViewModel",
            "Income per week: ${incomePerWeek.joinToString(", ")}"
        )

        expenses.forEach {
            try {
                val date = LocalDate.parse(it.Date ?: "", formatter)
                if (date.monthValue == currentMonth && date.year == currentYear) {
                    val week = getWeekOfMonth(date)
                    expensePerWeek[week] += it.Amount.toFloat()
                }
            } catch (_: Exception) { }
        }

        return List(5) { i ->
            WeeklyTotals(
                week = "Week ${i + 1}",
                income = incomePerWeek[i],
                expense = expensePerWeek[i]
            )
        }
    }


    fun calculateTotalSpentPerFriend(expenses: List<Expense>): Map<String, Double> {
        val friendSpendingMap = mutableMapOf<String, Double>()

        for (expense in expenses) {
            for (friend in expense.Friends) {
                val current = friendSpendingMap[friend.name] ?: 0.0
                friendSpendingMap[friend.name] = current + expense.MyContribution
            }
        }

        // Sort by amount descending
        return friendSpendingMap.toList()
            .sortedByDescending { (_, amount) -> amount }
            .toMap()
    }

    fun getCategoryCounts(expenses: List<Expense>, filter: String): CategoryStats {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun isInFilter(dateString: String?): Boolean {
            if (dateString.isNullOrBlank()) return false
            val date = dateFormat.parse(dateString) ?: return false
            val expenseCal = Calendar.getInstance().apply { time = date }

            return when (filter) {
                "This Week" -> calendar.get(Calendar.WEEK_OF_YEAR) == expenseCal.get(Calendar.WEEK_OF_YEAR) &&
                        calendar.get(Calendar.YEAR) == expenseCal.get(Calendar.YEAR)
                "This Month" -> calendar.get(Calendar.MONTH) == expenseCal.get(Calendar.MONTH) &&
                        calendar.get(Calendar.YEAR) == expenseCal.get(Calendar.YEAR)
                "Last Month" -> {
                    val lastMonth = calendar.clone() as Calendar
                    lastMonth.add(Calendar.MONTH, -1)
                    lastMonth.get(Calendar.MONTH) == expenseCal.get(Calendar.MONTH) &&
                            lastMonth.get(Calendar.YEAR) == expenseCal.get(Calendar.YEAR)
                }
                "This Year" -> calendar.get(Calendar.YEAR) == expenseCal.get(Calendar.YEAR)
                else -> true
            }
        }

        val filtered = expenses.filter { isInFilter(it.Date) }

        val grouped = filtered.groupingBy { it.ExpenseType.trim() }.eachCount()

        val categoryLabels = listOf("Food", "Shopping", "Entertainment", "Subscriptions", "Travel", "Other")

        val categoryAmountMap = mutableMapOf<String, Double>()
        for (category in categoryLabels) {
            val totalAmount = filtered
                .filter { it.ExpenseType.equals(category, ignoreCase = true) }
                .sumOf { it.Amount }

            if (totalAmount > 0.0) {
                categoryAmountMap[category] = totalAmount
            }
        }

        val result = categoryLabels.map { label ->
            val count = grouped[label] ?: 0
            CategoryCount(label, count)
        }.filter { it.count > 0 }

        return CategoryStats(counts = result, amounts = categoryAmountMap)
    }



    fun resetCategoryCounts() {
        val categories = listOf("Food", "Shopping", "Entertainment", "Subscriptions", "Travel", "Other")
        categories.map { category -> CategoryCount(category, 0) }
    }

}