package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trackify.R
import com.example.trackify.ui.theme.latoFontFamily
import com.example.trackify.viewmodel.AuthViewModel
import com.example.trackify.viewmodel.UtilityViewModel
import kotlin.math.cos
import kotlin.math.sin


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val authViewModel: AuthViewModel = viewModel()
    val utilityViewModel: UtilityViewModel = viewModel()
    val netBalance by utilityViewModel.netBalanceText.collectAsState()
    val recentExpenses by utilityViewModel.last4Expenses.collectAsState()
    val userName by authViewModel.userName

    authViewModel.fetchUserName()

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val windowInsetsController = window?.let { WindowCompat.getInsetsController(it, view) }

    if (windowInsetsController != null) {
        windowInsetsController.isAppearanceLightStatusBars = false
    }

    val type = listOf(
        "Food",
        "Shopping",
        "Subsciptions",
        "Entertainment",
        "Travel",
    )

    val typeImgs = listOf(
        R.drawable.food,
        R.drawable.shopping,
        R.drawable.netflix,
        R.drawable.tv,
        R.drawable.travel
    )

    LaunchedEffect(Unit) {
        utilityViewModel.startListeningToNetBalance()
    }

    LaunchedEffect(Unit) {
        utilityViewModel.startListeningToExpenses()
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                        .padding(
                            horizontal = 0.04 * screenWidth
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Row{
                                    Image(
                                        painter = painterResource(R.drawable.man),
                                        contentDescription = "Menu Icon",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(shape = androidx.compose.foundation.shape.CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "Hi, " + if(userName == null) "Loading..." else userName,
                                        color = Color.White,
                                        fontFamily = latoFontFamily,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(vertical = 8.dp),
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    painter = painterResource(R.drawable.notification),
                                    contentDescription = "Menu Icon",
                                    modifier = Modifier
                                        .size(38.dp)
                                        .align(Alignment.CenterVertically)
                                        .clickable {
                                            authViewModel.logout()
                                            navController.navigate("start") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        },
                                    tint = Color(0xFFDE4251)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.035 * screenHeight))
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .fillMaxWidth()
                                    .height(0.185 * screenHeight)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFD72C3F), // starting color
                                                Color(0xFFB64651), // darker red (you can adjust)
                                                Color(0xFFDE4251)  // lighter red (you can adjust)
                                            )
                                        )
                                    )
                            ){
                                Row(
                                    modifier = Modifier
                                        .padding(start = 15.dp, top = 10.dp, end = 15.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.TopStart),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(
                                        text = "Total Balance",
                                        color = Color.White,
                                        fontFamily = latoFontFamily,
                                        fontSize = 16.sp,
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Text(
                                            text = "View Statistics",
                                            color = Color.White,
                                            fontFamily = latoFontFamily,
                                            fontSize = 16.sp,
                                        )
                                        Icon(
                                            Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                            contentDescription = "Arrow Icon",
                                            modifier = Modifier
                                                .size(20.dp)
                                                .align(Alignment.CenterVertically),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = if (netBalance == null) "Loading..." else " ₹${String.format("%.2f", netBalance)}",
                                    color = Color.White,
                                    fontFamily = latoFontFamily,
                                    modifier = Modifier
                                        .padding(start = 15.dp)
                                        .align(Alignment.CenterStart),
                                    fontSize = 38.sp,
                                )

                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.035 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Recent Spendings",
                                    color = Color.White,
                                    fontFamily = latoFontFamily,
                                    modifier = Modifier,
                                    fontSize = 19.sp,
                                )
                                Text(
                                    text = "See all",
                                    color = Color(0xFFDE4251),
                                    fontFamily = latoFontFamily,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate("viewTransactions")
                                        },
                                    fontSize = 16.sp,
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.015 * screenHeight))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp)
                                    .background(
                                        color = Color(0x14ABABAB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0x66ABABAB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                if (recentExpenses.isEmpty()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp),
                                        color = Color.White,
                                        strokeWidth = 5.dp
                                    )
                                } else {
                                    recentExpenses.forEachIndexed { index, expense ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 10.dp,
                                                    end = 10.dp,
                                                    top = 0.012 * screenHeight
                                                ),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    if (expense.ExpenseType == "Food") typeImgs[0]
                                                    else if (expense.ExpenseType == "Shopping") typeImgs[1]
                                                    else if (expense.ExpenseType == "Subscription") typeImgs[2]
                                                    else if (expense.ExpenseType == "Entertainment") typeImgs[3]
                                                    else if (expense.ExpenseType == "Travel") typeImgs[4]
                                                    else R.drawable.others // fallback icon
                                                ), // adjust as needed
                                                contentDescription = "Transaction Icon",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(48.dp),
                                            )
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 12.dp)
                                            ) {
                                                Text(
                                                    text = expense.Title,
                                                    color = Color.White,
                                                    fontFamily = latoFontFamily,
                                                    fontSize = 16.sp,
                                                )
                                                Text(
                                                    text = expense.ExpenseType,
                                                    color = Color.Gray,
                                                    fontFamily = latoFontFamily,
                                                    fontSize = 14.sp,
                                                )
                                            }
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "- ₹${expense.MyContribution}",
                                                    color = Color.White,
                                                    fontFamily = latoFontFamily,
                                                    fontSize = 14.sp,
                                                )
                                                Text(
                                                    text = expense.Date ?: "Unknown Date",
                                                    color = Color.Gray,
                                                    fontFamily = latoFontFamily,
                                                    fontSize = 14.sp,
                                                )
                                            }
                                        }

                                        if (index == recentExpenses.lastIndex) {
                                            Spacer(modifier = Modifier.height(0.012 * screenHeight))
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.032 * screenHeight))
                            Text(
                                text = "Spending Overview",
                                color = Color.White,
                                fontFamily = latoFontFamily,
                                modifier = Modifier,
                                fontSize = 19.sp,
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.015 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(
                                            start = 0.05 * screenWidth,
                                            top = 0.05 * screenWidth
                                        )
                                        .size(0.38 * screenWidth)
                                ){
                                    Image(
                                        painter = painterResource(R.drawable.income),
                                        contentDescription = "Transaction Icon",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Monthly Income",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontFamily = latoFontFamily,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = "₹20,000",
                                        color = Color(0xFFDADADA),
                                        fontSize = 26.sp,
                                        fontFamily = latoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(
                                            start = 0.05 * screenWidth,
                                            top = 0.05 * screenWidth
                                        )
                                        .size(0.38 * screenWidth)
                                ){
                                    Image(
                                        painter = painterResource(R.drawable.spending),
                                        contentDescription = "Transaction Icon",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Monthly Expenses",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontFamily = latoFontFamily,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = "₹18,000",
                                        color = Color(0xFFDADADA),
                                        fontSize = 26.sp,
                                        fontFamily = latoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(
                                            start = 0.05 * screenWidth,
                                            top = 0.05 * screenWidth
                                        )
                                        .size(0.38 * screenWidth)
                                ){
                                    Image(
                                        painter = painterResource(R.drawable.friends),
                                        contentDescription = "Transaction Icon",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "With Friends",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontFamily = latoFontFamily,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = "₹5,600",
                                        color = Color(0xFFDADADA),
                                        fontSize = 26.sp,
                                        fontFamily = latoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(
                                            start = 0.05 * screenWidth,
                                            top = 0.05 * screenWidth
                                        )
                                        .size(0.38 * screenWidth)
                                ){
                                    Image(
                                        painter = painterResource(R.drawable.graph),
                                        contentDescription = "Transaction Icon",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Expenses Summary",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontFamily = latoFontFamily,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = "View All",
                                        color = Color(0xFFDADADA),
                                        fontSize = 24.sp,
                                        fontFamily = latoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(0.15 * screenHeight))
                        }
                    }



                    //bottom navigation bar
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 0.045 * screenWidth,
                                end = 0.045 * screenWidth,
                                bottom = 0.045 * screenHeight
                            )
                            .align(Alignment.BottomCenter),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Row(
                            modifier = Modifier
                                .background(
                                    shape = RoundedCornerShape(
                                        50.dp
                                    ),
                                    color = Color(0xFF19191B)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .height(65.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .size(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.home),
                                    contentDescription = "home",
                                    Modifier.size(36.dp),
                                    tint = Color(0xFFDE4251)
                                )
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            IconButton(
                                onClick = {
                                    navController.navigate("viewTransactions")
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .size(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.transaction_nn),
                                    contentDescription = "transaction",
                                    Modifier.size(36.dp),
                                    tint = Color(0xFFFFFFFF)
                                )
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .size(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.report_n),
                                    contentDescription = "reports",
                                    Modifier.size(30.dp),
                                    tint = Color(0xFFFFFFFF)
                                )
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            IconButton(
                                onClick = {
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .size(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.user),
                                    contentDescription = "user",
                                    Modifier.size(32.dp),
                                    tint = Color(0xFFFFFFFF)
                                )
                            }
                        }
                        Spacer( modifier = Modifier.width(4.dp))
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("transaction")
                            },
                            containerColor = Color(0xFFDE4251),
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                focusedElevation = 0.dp,
                                hoveredElevation = 0.dp
                            ),
                            modifier = Modifier
                                .size(0.09 * screenHeight)

                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Add Transaction",
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    HomePage(rememberNavController())
}
