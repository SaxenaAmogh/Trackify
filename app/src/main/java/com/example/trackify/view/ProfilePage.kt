package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.trackify.R
import com.example.trackify.ui.theme.latoFontFamily
import com.example.trackify.viewmodel.AuthViewModel
import com.example.trackify.viewmodel.ReportsViewModel
import com.example.trackify.viewmodel.UtilityViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(navController: NavHostController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val windowInsetsController = window?.let { WindowCompat.getInsetsController(it, view) }
    if (windowInsetsController != null) {
        windowInsetsController.isAppearanceLightStatusBars = false
    }

    val auth = FirebaseAuth.getInstance()
    val authViewModel: AuthViewModel = viewModel()
    authViewModel.fetchUserName()
    val userName by authViewModel.userName
    val email = auth.currentUser?.email

    val utilityViewModel: UtilityViewModel = viewModel()
    val reportsViewModel: ReportsViewModel = viewModel()
    utilityViewModel.fetchAllIncomes()
    utilityViewModel.fetchAllRawExpenses()
    val allExpensesRaw by utilityViewModel.allExpensesRaw.collectAsState()
    val allIncomes by utilityViewModel.allIncomes.collectAsState()
    var incomeTotal by remember { mutableDoubleStateOf(0.0) }
    var expenseTotal by remember { mutableDoubleStateOf(0.0) }
    val (income, expense) = reportsViewModel.getFilteredIncomeAndExpenseTotal("This Month", allExpensesRaw, allIncomes)
    incomeTotal = income
    expenseTotal = expense

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
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFDE4251)
                                )
                                .height(
                                    0.34 * screenHeight,
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ){
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF000000),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ){
                                    Icon(
                                        Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                        contentDescription = "Arrow Icon",
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(32.dp)
                                            .align(Alignment.CenterStart)
                                            .clickable {
                                                navController.popBackStack()
                                            },
                                        tint = Color.White
                                    )
                                }
                                Image(
                                    painter = painterResource(R.drawable.man),
                                    contentDescription = "Menu Icon",
                                    modifier = Modifier
                                        .size(0.32 * screenWidth)
                                        .clip(shape = androidx.compose.foundation.shape.CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Icon(
                                    painter = painterResource(R.drawable.logout),
                                    contentDescription = "Menu Icon",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable {
                                            authViewModel.logout()
                                            navController.navigate("start") {
                                                popUpTo("profile") { inclusive = true }
                                            }
                                        },
                                    tint = Color(0xFF000000)
                                )
                            }
                            userName?.let { it1 ->
                                Text(
                                    text = it1,
                                    modifier = Modifier
                                        .padding(
                                            top = 0.01 * screenHeight,
                                        )
                                        .align(Alignment.CenterHorizontally),
                                    color = Color.White,
                                    fontFamily = latoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                            Text(
                                text = email ?: "No Email",
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                color = Color(0xFF232323),
                                fontFamily = latoFontFamily,
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(
                                    y = -0.03 * screenHeight
                                )
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(
                                        topStart = 30.dp,
                                        topEnd = 30.dp
                                    )
                                )
                                .heightIn(
                                    min = 0.2 * screenHeight,
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 20.dp)
                                    .background(
                                        color = Color(0x14ABABAB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0x66ABABAB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .heightIn(
                                        min = 0.2 * screenHeight,
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Transaction Summary",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontFamily = latoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "( This Month )",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        fontFamily = latoFontFamily,
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 0.1 * screenWidth,
                                            end = 0.1 * screenWidth,
                                            bottom = 16.dp
                                        ),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(0.4f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            VerticalDivider(
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .clip(
                                                        RoundedCornerShape(50.dp)
                                                    ),
                                                color = Color(0xFF00BCD4),
                                                thickness = 3.dp,
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Income",
                                                color = Color.White,
                                                fontSize = 19.sp,
                                                fontFamily = latoFontFamily,
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            text = if (incomeTotal == null) "Loading..." else " ₹${
                                                String.format(
                                                    "%.2f",
                                                    incomeTotal
                                                )
                                            }", //
                                            color = Color(0xFFFFFFFF),
                                            fontSize = 28.sp,
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(0.4f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            VerticalDivider(
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .clip(
                                                        RoundedCornerShape(50.dp)
                                                    ),
                                                color = Color(0xFFDE4251),
                                                thickness = 3.dp,
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Expenses",
                                                color = Color.White,
                                                fontSize = 19.sp,
                                                fontFamily = latoFontFamily,
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            text = if (expenseTotal == null) "Loading..." else " ₹${
                                                String.format(
                                                    "%.2f",
                                                    expenseTotal
                                                )
                                            }", //
                                            color = Color(0xFFFFFFFF),
                                            fontSize = 28.sp,
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Settings Section
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                            Column{
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 20.dp,
                                                topEnd = 20.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 6.dp
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 20.dp,
                                                topEnd = 20.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 6.dp
                                            )
                                        )
                                ){
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row{
                                            Icon(
                                                painter = painterResource(R.drawable.edit),
                                                contentDescription = "Menu Icon",
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable {
                                                        authViewModel.logout()
                                                        navController.navigate("start") {
                                                            popUpTo("profile") { inclusive = true }
                                                        }
                                                    },
                                                tint = Color(0xFFDE4251)
                                            )
                                            Text(
                                                text = "Edit Profile",
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .align(Alignment.CenterVertically),
                                                color = Color(0xFFFFFFFF),
                                                fontSize = 18.sp,
                                                fontFamily = latoFontFamily,
                                            )
                                        }
                                        Icon(
                                            Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                            contentDescription = "Arrow Icon",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable {
                                                    navController.popBackStack()
                                                },
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 6.dp
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 6.dp
                                            )
                                        )
                                ){
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row{
                                            Icon(
                                                painter = painterResource(R.drawable.reset),
                                                contentDescription = "Menu Icon",
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable {
                                                        authViewModel.logout()
                                                        navController.navigate("start") {
                                                            popUpTo("profile") { inclusive = true }
                                                        }
                                                    },
                                                tint = Color(0xFFDE4251)
                                            )
                                            Text(
                                                text = "Change Password",
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .align(Alignment.CenterVertically),
                                                color = Color(0xFFFFFFFF),
                                                fontSize = 18.sp,
                                                fontFamily = latoFontFamily,
                                            )
                                        }
                                        Icon(
                                            Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                            contentDescription = "Arrow Icon",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable {
                                                    navController.popBackStack()
                                                },
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 6.dp
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 6.dp
                                            )
                                        )
                                ){
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row{
                                            Icon(
                                                painter = painterResource(R.drawable.logout),
                                                contentDescription = "Menu Icon",
                                                modifier = Modifier
                                                    .padding(start = 5.dp)
                                                    .size(32.dp)
                                                    .clickable {
                                                        authViewModel.logout()
                                                        navController.navigate("start") {
                                                            popUpTo("profile") { inclusive = true }
                                                        }
                                                    },
                                                tint = Color(0xFFDE4251)
                                            )
                                            Text(
                                                text = "Logout",
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .align(Alignment.CenterVertically),
                                                color = Color(0xFFFFFFFF),
                                                fontSize = 18.sp,
                                                fontFamily = latoFontFamily,
                                            )
                                        }
                                        Icon(
                                            Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                            contentDescription = "Arrow Icon",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable {
                                                    navController.popBackStack()
                                                },
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(
                                            color = Color(0x14ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 20.dp,
                                                bottomEnd = 20.dp
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0x66ABABAB),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 20.dp,
                                                bottomEnd = 20.dp
                                            )
                                        )
                                        .clickable {
                                            Toast.makeText(
                                                context,
                                                "Feedback feature coming soon!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                ){
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                Toast.makeText(
                                                    context,
                                                    "Feedback feature coming soon!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row{
                                            Icon(
                                                painter = painterResource(R.drawable.feedback),
                                                contentDescription = "Menu Icon",
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable {
                                                        Toast.makeText(
                                                            context,
                                                            "Feedback feature coming soon!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                tint = Color(0xFFDE4251)
                                            )
                                            Text(
                                                text = "Add Feedback",
                                                modifier = Modifier
                                                    .clickable {
                                                        Toast.makeText(
                                                            context,
                                                            "Feedback feature coming soon!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .padding(start = 8.dp)
                                                    .align(Alignment.CenterVertically),
                                                color = Color(0xFFFFFFFF),
                                                fontSize = 18.sp,
                                                fontFamily = latoFontFamily,
                                            )
                                        }
                                        Icon(
                                            Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                            contentDescription = "Arrow Icon",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable {
                                                    Toast.makeText(
                                                        context,
                                                        "Feedback feature coming soon!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage(rememberNavController())
}