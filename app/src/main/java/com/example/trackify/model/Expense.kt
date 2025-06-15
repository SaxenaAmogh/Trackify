package com.example.trackify.model

data class FriendEntry(
    val name: String = "",
    val contribution: Double = 0.0,
    val paid: Boolean = false
)

data class Expense(
    val Title: String = "",
    val Amount: Double = 0.0,
    val PaymentType: String = "",
    val Date: String? = null,
    val ExpenseType: String = "",
    val Friends: List<FriendEntry> = emptyList(),
    val MyContribution: Double = 0.0,
    val Note: String = ""
)

data class IncomeData(
    val Title: String = "",
    val Amount: Double = 0.0,
    val PaymentType: String = "",
    val Date: String = "",
    val Note: String = ""
)

