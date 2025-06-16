package com.example.trackify.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trackify.viewmodel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController) {

    val transactionViewModel: TransactionViewModel = viewModel()
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null

    LaunchedEffect(Unit) {
        if (isUserLoggedIn) {
            navController.navigate("reports") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) "reports" else "start" // Start with the TransactionPage,
    ) {
        composable("home") {
            HomePage(navController)
        }
        composable("start") {
            StartPage(navController)
        }
        composable("transaction") {
            AddTransactionPage(navController, transactionViewModel)
        }
        composable("friends") {
            AddFriendsPage(navController, transactionViewModel)
        }
        composable("login") {
            LoginPage(navController)
        }
        composable("signup") {
            CreatePage(navController)
        }
        composable("viewTransactions") {
            TransactionsPage(navController)
        }
        composable("reports") {
            ReportsPage(navController)
        }
    }
}
