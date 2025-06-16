package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
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
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trackify.R
import com.example.trackify.model.Expense
import com.example.trackify.model.IncomeData
import com.example.trackify.ui.theme.latoFontFamily
import com.example.trackify.viewmodel.UtilityViewModel
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionsPage(navController: NavController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val focusManager = LocalFocusManager.current

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val windowInsetsController = window?.let { WindowCompat.getInsetsController(it, view) }
    if (windowInsetsController != null) {
        windowInsetsController.isAppearanceLightStatusBars = false
    }

    val utilityViewModel: UtilityViewModel = viewModel()
    val allExpensesRaw by utilityViewModel.allExpensesRaw.collectAsState()
    val allIncomes by utilityViewModel.allIncomes.collectAsState()
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }
    var selectedIncome by remember { mutableStateOf<IncomeData?>(null) }

    var searchItem by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expenses") }
    var expanded by remember { mutableStateOf(false) }
    val transactionsType = listOf("Expenses", "Income")

    var sort by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var paymentType by remember { mutableStateOf("") }

    var showSheet by remember { mutableStateOf(false) }
    var showSheet2 by remember { mutableStateOf(false) }
    var showSheet3 by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val typeImgs = listOf(
        R.drawable.food,
        R.drawable.shopping,
        R.drawable.netflix,
        R.drawable.tv,
        R.drawable.travel
    )

    val payImgs = listOf(
        R.drawable.upi,
        R.drawable.cash,
        R.drawable.creditcard,
        R.drawable.mobilebanking,
        R.drawable.income
    )

    var selected1 by remember { mutableStateOf(false) }
    var selected2 by remember { mutableStateOf(false) }
    var selected3 by remember { mutableStateOf(false) }
    var selected4 by remember { mutableStateOf(false) }

    @SuppressLint("DefaultLocale")
    @Composable
    fun showExpenses(){

        Column(
            modifier = Modifier
                .fillMaxWidth()
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
            if (allExpensesRaw.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    color = Color.White,
                    strokeWidth = 5.dp
                )
            } else {
                // ✅ Step 1: Sort expenses
                val sortedExpenses = when (sortBy) {
                    "High to Low" -> allExpensesRaw.sortedByDescending { it.MyContribution }
                    "Low to High" -> allExpensesRaw.sortedBy { it.MyContribution }
                    "Newest to Oldest" -> allExpensesRaw.sortedByDescending {
                        LocalDate.parse(it.Date, formatter)
                    }
                    "Oldest to Newest" -> allExpensesRaw.sortedBy {
                        LocalDate.parse(it.Date, formatter)
                    }
                    else -> allExpensesRaw
                }

                // ✅ Step 2: Filter sorted list by search & category
                val filteredExpenses = sortedExpenses.filter { expense ->
                    val matchesSearch = searchItem.isBlank() || expense.Title.contains(searchItem, ignoreCase = true)
                    val matchesCategory = category.isBlank() || expense.ExpenseType.contains(category, ignoreCase = true)
                    matchesSearch && matchesCategory
                }

                // ✅ Step 3: Show message if nothing found
                if (filteredExpenses.isEmpty()) {
                    Text(
                        text = "No expenses found.",
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontFamily = latoFontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // ✅ Step 4: Loop through final list
                    filteredExpenses.forEachIndexed { index, expense ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 0.018 * screenHeight,
                                    bottom = 0.01 * screenHeight
                                )
                                .clickable {
                                    selectedExpense = expense
                                    showSheet = true
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
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
                                modifier = Modifier.size(56.dp),
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 20.dp)
                            ) {
                                Text(
                                    text = expense.Title,
                                    color = Color.White,
                                    fontFamily = latoFontFamily,
                                    fontSize = 17.sp,
                                )
                                Text(
                                    text = " ₹${String.format("%.2f", expense.MyContribution)}",
                                    color = Color.Gray,
                                    fontFamily = latoFontFamily,
                                    fontSize = 16.sp,
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                contentDescription = "Arrow Icon",
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(32.dp)
                                    .clickable {
                                        selectedExpense = expense
                                        showSheet = true
                                    },
                                tint = Color.White
                            )
                        }
                        if (index != allExpensesRaw.lastIndex) {
                            Divider(
                                color = Color(0x66ABABAB),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(0.015 * screenHeight))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(0.2 * screenHeight))
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                scrimColor = Color.Transparent,
                containerColor = Color.Transparent,
                dragHandle = null,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF525252).copy(alpha = 0.09f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .height(0.7 * screenHeight),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(0.15 * screenWidth)
                            .padding(top = 20.dp)
                            .height(5.dp)
                            .background(
                                Color(0xFFDE4251),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 0.015 * screenHeight,
                                    start = 0.035 * screenWidth,
                                    end = 0.035 * screenWidth
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        if (selectedExpense?.ExpenseType == "Food") typeImgs[0]
                                        else if (selectedExpense?.ExpenseType == "Shopping") typeImgs[1]
                                        else if (selectedExpense?.ExpenseType == "Subscription") typeImgs[2]
                                        else if (selectedExpense?.ExpenseType == "Entertainment") typeImgs[3]
                                        else if (selectedExpense?.ExpenseType == "Travel") typeImgs[4]
                                        else R.drawable.others
                                    ),
                                    contentDescription = "shopping",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(0.12 * screenHeight)
                                        .padding(
                                            start = 8.dp,
                                            end = 4.dp,
                                            top = 5.dp,
                                            bottom = 5.dp
                                        ),
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row {
                                        Text(
                                            "Money Spent:",
                                            fontSize = 22.sp,
                                            color = Color.White,
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                        )
                                        Text(
                                            " ₹${String.format("%.2f", selectedExpense?.MyContribution)}",
                                            fontSize = 22.sp,
                                            color = Color(0xFFDE4251),
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    selectedExpense?.Title?.let { it1 ->
                                        Text(
                                            it1,
                                            fontSize = 18.sp,
                                            color = Color.White,
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    selectedExpense?.Date?.let {
                                        Text(
                                            it,
                                            fontSize = 15.sp,
                                            color = Color(0xFFADADAD),
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Total Amount: ",
                                    color = Color(0xFFADADAD),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Text(
                                    text = " ₹${String.format("%.2f", selectedExpense?.Amount)}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    fontFamily = latoFontFamily,
                                    modifier = Modifier
                                )
                            }
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Payment Type: ",
                                    color = Color(0xFFADADAD),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    textDecoration = TextDecoration.Underline,
                                )
                                selectedExpense?.PaymentType?.let { it1 ->
                                    Text(
                                        text = it1,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W500,
                                        fontFamily = latoFontFamily,
                                        modifier = Modifier
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                            Text(
                                text = "Friends' Shares: ",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    ),
                                color = Color(0xFFADADAD),
                                fontFamily = latoFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W500,
                                textDecoration = TextDecoration.Underline,
                            )
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                            Column(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    )
                                    .background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.4.dp,
                                        color = Color(0x66ABABAB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            top = 0.014 * screenHeight,
                                            bottom = 0.010 * screenHeight
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Name",
                                        color = Color(0xFFDE4251),
                                        modifier = Modifier
                                            .weight(0.45f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Share",
                                        color = Color(0xFFDE4251),
                                        modifier = Modifier
                                            .weight(0.3f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Paid",
                                        color = Color(0xFFDE4251),
                                        modifier = Modifier
                                            .weight(0.3f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (selectedExpense?.Friends?.isEmpty() == true) {
                                    Text(
                                        text = "No friends added.",
                                        color = Color.White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        fontFamily = latoFontFamily,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    selectedExpense?.Friends?.forEach { friend ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 10.dp,
                                                    end = 10.dp,
                                                    top = 0.010 * screenHeight,
                                                    bottom = 0.010 * screenHeight
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = friend.name,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .weight(0.45f),
                                                fontFamily = latoFontFamily,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = " ₹${String.format("%.2f",friend.contribution)}",
                                                color = Color.White,
                                                modifier = Modifier
                                                    .weight(0.3f),
                                                fontFamily = latoFontFamily,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = if (friend.paid) "Yes" else "No",
                                                color = Color.White,
                                                modifier = Modifier
                                                    .weight(0.3f),
                                                fontFamily = latoFontFamily,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    ),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Text(
                                    text = "Note:",
                                    color = Color(0xFFADADAD),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                selectedExpense?.Note?.ifEmpty { "No note provided." }
                                    ?.let { it1 ->
                                        Text(
                                            text = it1,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.W500,
                                            fontFamily = latoFontFamily,
                                            modifier = Modifier
                                        )
                                    }
                            }
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun showIncomes(){

        Column(
            modifier = Modifier
                .fillMaxWidth()
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
            if (allIncomes.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    color = Color.White,
                    strokeWidth = 5.dp
                )
            } else {
                // ✅ Step 1: Sort expenses
                val sortedIncome = when (sortBy) {
                    "High to Low" -> allIncomes.sortedByDescending { it.Amount }
                    "Low to High" -> allIncomes.sortedBy { it.Amount }
                    "Newest to Oldest" -> allIncomes.sortedByDescending {
                        LocalDate.parse(it.Date, formatter)
                    }
                    "Oldest to Newest" -> allIncomes.sortedBy {
                        LocalDate.parse(it.Date, formatter)
                    }
                    else -> allIncomes
                }

                // ✅ Step 2: Filter sorted list by search & category
                val filteredIncome = sortedIncome.filter { income ->
                    val matchesSearch = searchItem.isBlank() || income.Title.contains(searchItem, ignoreCase = true)
                    val matchesPaymentType = paymentType.isBlank() || income.PaymentType.contains(paymentType, ignoreCase = true)
                    matchesSearch && matchesPaymentType
                }

                // ✅ Step 3: Show message if nothing found
                if (filteredIncome.isEmpty()) {
                    Text(
                        text = "No expenses found.",
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontFamily = latoFontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // ✅ Step 4: Loop through final list
                    filteredIncome.forEachIndexed { index, income ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 0.018 * screenHeight,
                                    bottom = 0.01 * screenHeight
                                )
                                .clickable {
                                    selectedIncome = income
                                    showSheet3 = true
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Image(
                                painter = painterResource(
                                    if (income.PaymentType == "UPI") payImgs[0]
                                    else if (income.PaymentType == "Cash") payImgs[1]
                                    else if (income.PaymentType == "Card") payImgs[2]
                                    else if (income.PaymentType == "Net Banking") payImgs[3]
                                    else R.drawable.others // fallback icon
                                ), // adjust as needed
                                contentDescription = "Icon",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(56.dp),
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 20.dp)
                            ) {
                                Text(
                                    text = income.Title,
                                    color = Color.White,
                                    fontFamily = latoFontFamily,
                                    fontSize = 17.sp,
                                )
                                Text(
                                    text = " ₹${String.format("%.2f", income.Amount)}",
                                    color = Color.Gray,
                                    fontFamily = latoFontFamily,
                                    fontSize = 16.sp,
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                                contentDescription = "Arrow Icon",
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(32.dp)
                                    .clickable {
                                        selectedIncome = income
                                        showSheet3 = true
                                    },
                                tint = Color.White
                            )
                        }
                        if (index != allExpensesRaw.lastIndex) {
                            Divider(
                                color = Color(0x66ABABAB),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(0.015 * screenHeight))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(0.2 * screenHeight))
        if (showSheet3) {
            ModalBottomSheet(
                onDismissRequest = { showSheet3 = false },
                sheetState = sheetState,
                scrimColor = Color.Transparent,
                containerColor = Color.Transparent,
                dragHandle = null,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF676767).copy(alpha = 0.09f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .height(0.3 * screenHeight),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(0.15 * screenWidth)
                            .padding(top = 20.dp)
                            .height(5.dp)
                            .background(
                                Color(0xFFDE4251),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 0.015 * screenHeight,
                                    start = 0.035 * screenWidth,
                                    end = 0.035 * screenWidth
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        if (selectedIncome?.PaymentType == "UPI") payImgs[0]
                                        else if (selectedIncome?.PaymentType == "Cash") payImgs[1]
                                        else if (selectedIncome?.PaymentType == "Card") payImgs[2]
                                        else if (selectedIncome?.PaymentType == "Net Banking") payImgs[3]
                                        else R.drawable.others
                                    ),
                                    contentDescription = "images",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(0.12 * screenHeight)
                                        .padding(
                                            start = 8.dp,
                                            end = 4.dp,
                                            top = 5.dp,
                                            bottom = 5.dp
                                        ),
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row {
                                        Text(
                                            "Income:",
                                            fontSize = 22.sp,
                                            color = Color.White,
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                        )
                                        Text(
                                            " ₹${String.format("%.2f", selectedIncome?.Amount)}",
                                            fontSize = 22.sp,
                                            color = Color(0xFFDE4251),
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    selectedIncome?.Title?.let { it1 ->
                                        Text(
                                            it1,
                                            fontSize = 18.sp,
                                            color = Color.White,
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    selectedIncome?.Date?.let {
                                        Text(
                                            it,
                                            fontSize = 15.sp,
                                            color = Color(0xFFADADAD),
                                            fontFamily = latoFontFamily,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Payment Type: ",
                                    color = Color(0xFFADADAD),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    textDecoration = TextDecoration.Underline,
                                )
                                selectedIncome?.PaymentType?.let { it1 ->
                                    Text(
                                        text = it1,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W500,
                                        fontFamily = latoFontFamily,
                                        modifier = Modifier
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.035 * screenWidth
                                    ),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Text(
                                    text = "Note:",
                                    color = Color(0xFFADADAD),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                selectedIncome?.Note?.ifEmpty { "No note provided." }
                                    ?.let { it1 ->
                                        Text(
                                            text = it1,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.W500,
                                            fontFamily = latoFontFamily,
                                            modifier = Modifier
                                        )
                                    }
                            }
                            Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun sorting(){
        Row(
            modifier = Modifier
                .background(
                    if (sort) Color(0xFFDE4251) else Color.Transparent,
                    shape = RoundedCornerShape(24)
                )
                .border(
                    width = 1.dp,
                    color = if (!sort) Color(0x66ABABAB) else Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(24)
                )
                .clickable {
                    showSheet2 = !showSheet2
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.filter),
                contentDescription = "Dropdown",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 12.dp)
                    .size(22.dp)
                    .clickable {
                        showSheet2 = !showSheet2
                    }
            )
            Text(
                text = "Sort",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showSheet2 = !showSheet2
                    }
                    .padding(
                        start = 8.dp,
                        end = 4.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ),
                fontFamily = latoFontFamily,
                fontSize = 16.sp,
                color = Color.White
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp)
                    .clickable {
                        showSheet2 = !showSheet2
                    }
            )
        }

        if (showSheet2) {
            ModalBottomSheet(
                onDismissRequest = { showSheet2 = false },
                sheetState = sheetState,
                scrimColor = Color.Transparent,
                containerColor = Color.Transparent,
                dragHandle = null,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF525252).copy(alpha = 0.09f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .height(0.4 * screenHeight),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(0.15 * screenWidth)
                            .padding(top = 20.dp)
                            .height(5.dp)
                            .background(
                                Color(0xFFDE4251),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        Text(
                            "Sort Transactions",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 0.015 * screenHeight,
                                    start = 0.035 * screenWidth,
                                ),
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = latoFontFamily,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color(0xFFADADAD),
                            thickness = 1.dp
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 0.05 * screenWidth,
                                    end = 0.05 * screenWidth
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.02 * screenWidth,
                                        vertical = 0.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Amount: High to Low",
                                    color = Color(0xFFFFFFFF),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                )
                                RadioButton(
                                    selected = selected1,
                                    onClick = {
                                        selected1 = true
                                        selected2 = false
                                        selected3 = false
                                        selected4 = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFDE4251),
                                        unselectedColor = Color(0xFFB2B2B2),
                                        disabledSelectedColor = Color(0xFFDE4251).copy(alpha = 0.5f),
                                        disabledUnselectedColor = Color(0xFFB2B2B2).copy(alpha = 0.5f)
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.02 * screenWidth
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Amount: Low to High",
                                    color = Color(0xFFFFFFFF),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                )
                                RadioButton(
                                    selected = selected2,
                                    onClick = {
                                        selected1 = false
                                        selected3 = false
                                        selected4 = false
                                        selected2 = true },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFDE4251),
                                        unselectedColor = Color(0xFFB2B2B2),
                                        disabledSelectedColor = Color(0xFFDE4251).copy(alpha = 0.5f),
                                        disabledUnselectedColor = Color(0xFFB2B2B2).copy(alpha = 0.5f)
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.02 * screenWidth
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Date: Newest to Oldest",
                                    color = Color(0xFFFFFFFF),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                )
                                RadioButton(
                                    selected = selected3,
                                    onClick = {
                                        selected1 = false
                                        selected2 = false
                                        selected4 = false
                                        selected3 = true },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFDE4251),
                                        unselectedColor = Color(0xFFB2B2B2),
                                        disabledSelectedColor = Color(0xFFDE4251).copy(alpha = 0.5f),
                                        disabledUnselectedColor = Color(0xFFB2B2B2).copy(alpha = 0.5f)
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 0.02 * screenWidth
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Date: Oldest to Newest",
                                    color = Color(0xFFFFFFFF),
                                    fontFamily = latoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                )
                                RadioButton(
                                    selected = selected4,
                                    onClick = {
                                        selected1 = false
                                        selected3 = false
                                        selected2 = false
                                        selected4 = true },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFDE4251),
                                        unselectedColor = Color(0xFFB2B2B2),
                                        disabledSelectedColor = Color(0xFFDE4251).copy(alpha = 0.5f),
                                        disabledUnselectedColor = Color(0xFFB2B2B2).copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 12.dp
                                )
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    selected1 = false
                                    selected3 = false
                                    selected4 = false
                                    selected2 = false
                                    showSheet2 = false
                                    sort = false
                                },
                                modifier = Modifier
                                    .weight(0.3f)
                                    .border(
                                        width = 1.5.dp,
                                        color = Color(0xFFC93043),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                containerColor = Color.Transparent,
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp,
                                    focusedElevation = 0.dp,
                                    hoveredElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = "Clear",
                                    fontFamily = latoFontFamily,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            FloatingActionButton(
                                onClick = {
                                    sort = true
                                    sortBy = if (selected1) {
                                        "High to Low"
                                    } else if (selected2) {
                                        "Low to High"
                                    } else if (selected3) {
                                        "Newest to Oldest"
                                    } else if (selected4) {
                                        "Oldest to Newest"
                                    } else {
                                        ""
                                    }
                                    showSheet2 = false
                                    allExpensesRaw.shuffled()
                                },
                                modifier = Modifier
                                    .weight(0.5f),
                                containerColor = Color(0xFFDE4251),
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp,
                                    focusedElevation = 0.dp,
                                    hoveredElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = "Apply",
                                    fontFamily = latoFontFamily,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun expenseRow(){
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                sorting()
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (category != "Food") Color(0x66ABABAB) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (category == "Food") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            category = if (category != "Food") {
                                "Food"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.food),
                        contentDescription = "food",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Food",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (category != "Shopping") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (category == "Shopping") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            category = if (category != "Shopping") {
                                "Shopping"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.shopping),
                        contentDescription = "shopping",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Shopping",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (category != "Subscription") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (category == "Subscription") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            category = if (category != "Subscription") {
                                "Subscription"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.netflix),
                        contentDescription = "Subscription Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Subscriptions",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (category != "Entertainment") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (category == "Entertainment") Color(
                                0xFFDE4251
                            ) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            category = if (category != "Entertainment") {
                                "Entertainment"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.tv),
                        contentDescription = "Entertainment",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Entertainment",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (category != "Travel") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (category == "Travel") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            category = if (category != "Travel") {
                                "Travel"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.travel),
                        contentDescription = "Travel",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Travel",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (category != "Other") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (category == "Other") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            category = if (category != "Other") {
                                "Other"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.others),
                        contentDescription = "other",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Other",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    fun incomeRow(){
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                sorting()
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (paymentType != "Upi") Color(0x66ABABAB) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (paymentType == "Upi") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            paymentType = if (paymentType != "Upi") {
                                "Upi"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.upi),
                        contentDescription = "upi",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Upi",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (paymentType != "Cash") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (paymentType == "Cash") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            paymentType = if (paymentType != "Cash") {
                                "Cash"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.cash),
                        contentDescription = "cash",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Cash",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (paymentType != "Card") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (paymentType == "Card") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            paymentType = if (paymentType != "Card") {
                                "Card"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.creditcard),
                        contentDescription = "card",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Card",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (paymentType != "Net Banking") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (paymentType == "Net Banking") Color(
                                0xFFDE4251
                            ) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            paymentType = if (paymentType != "Net Banking") {
                                "Net Banking"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.mobilebanking),
                        contentDescription = "Net Banking",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Net Banking",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.width(0.02 * screenWidth))
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = if (paymentType != "Other") Color(
                                0x66ABABAB
                            ) else Color.White,
                            shape = RoundedCornerShape(24)
                        )
                        .background(
                            if (paymentType == "Other") Color(0xFFDE4251) else Color.Transparent,
                            shape = RoundedCornerShape(24)
                        )
                        .clickable {
                            paymentType = if (paymentType != "Other") {
                                "Other"
                            } else {
                                ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.income),
                        contentDescription = "other",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                    )
                    Text(
                        text = "Other",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 12.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        fontFamily = latoFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
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
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                        .padding(horizontal = 0.03 * screenWidth)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Transactions",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontFamily = latoFontFamily,
                            )
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(0.8f)
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .height(0.063 * screenHeight),
                                        shape = RoundedCornerShape(size = 28.dp),
                                        placeholder = {
                                            Text(
                                                "Search titles",
                                                fontFamily = latoFontFamily,
                                                color = Color(0xFFB2B2B2),
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.search),
                                                contentDescription = "Amount Icon",
                                                tint = Color(0xFFB2B2B2),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        value = searchItem,
                                        onValueChange = { searchItem = it },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Text
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                focusManager.clearFocus()
                                            }
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFDE4251),
                                            unfocusedBorderColor = Color(0x66ABABAB),
                                            focusedTextColor = Color(0xFFFFFFFF),
                                            unfocusedTextColor = Color(0xFFFFFFFF),
                                            unfocusedContainerColor = Color(0x14ABABAB),
                                            focusedContainerColor = Color(0x14ABABAB)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(0.01 * screenWidth))
                                Column(
                                    modifier = Modifier
                                        .weight(0.50f)
                                ) {
                                    Box {
                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = it }
                                        ) {
                                            OutlinedTextField(
                                                value = type,
                                                onValueChange = { type = it },
                                                readOnly = true,
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                trailingIcon = {
                                                    Icon(
                                                        Icons.Default.ArrowDropDown,
                                                        contentDescription = "Dropdown"
                                                    )
                                                },
                                                shape = RoundedCornerShape(16.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFDE4251),
                                                    unfocusedBorderColor = Color(0x66ABABAB),
                                                    unfocusedTextColor = Color(0xFFFFFFFF),
                                                    focusedTextColor = Color(0xFFFFFFFF),
                                                    unfocusedContainerColor = Color(0x14ABABAB),
                                                    focusedContainerColor = Color(0x14ABABAB)
                                                )
                                            )

                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier
                                                    .background(Color(0xFFFFFFFF)),
                                            ) {
                                                transactionsType.forEach { item ->
                                                    DropdownMenuItem(
                                                        colors = MenuItemColors(
                                                            textColor = Color.Black,
                                                            leadingIconColor = Color.Transparent,
                                                            trailingIconColor = Color.Transparent,
                                                            disabledTextColor = Color.Transparent,
                                                            disabledLeadingIconColor = Color.Transparent,
                                                            disabledTrailingIconColor = Color.Transparent,
                                                        ),
                                                        modifier = Modifier
                                                            .background(Color.White),
                                                        text = {
                                                            Text(
                                                                item,
                                                                fontFamily = latoFontFamily,
                                                                modifier = Modifier
                                                                    .fillMaxWidth(),
                                                                textAlign = TextAlign.Center
                                                            )
                                                        },
                                                        onClick = {
                                                            type = item
                                                            focusManager.clearFocus()
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        focusManager.clearFocus()
                                    })
                                }
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                if (type == "Income") {
                                    incomeRow()
                                } else {
                                    expenseRow()
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(0.02 * screenHeight))

                                utilityViewModel.fetchAllIncomes()
                                utilityViewModel.fetchAllRawExpenses()

                                if(type == "Expenses") {
                                    if (allExpensesRaw.isEmpty()) {
                                        Text(
                                            text = "No Expenses Found",
                                            color = Color(0xFFB2B2B2),
                                            fontFamily = latoFontFamily,
                                            fontSize = 18.sp,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        showExpenses()
                                    }
                                } else if(type == "Income") {
                                    if (allIncomes.isEmpty()) {
                                        Text(
                                            text = "No Income Found",
                                            color = Color(0xFFB2B2B2),
                                            fontFamily = latoFontFamily,
                                            fontSize = 18.sp,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        showIncomes()
                                    }
                                }
//                                Spacer(modifier = Modifier.height(0.1 * screenHeight))
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (showSheet || showSheet2 || showSheet3) Color(0xFF000000).copy(alpha = 0.9f) else Color.Transparent
                            )
                    ){}


                    //Bottom Navigation Bar
                    if (!showSheet && !showSheet2 && !showSheet3) {
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
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .size(55.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.home_n),
                                        contentDescription = "home",
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
                                        painter = painterResource(R.drawable.transaction),
                                        contentDescription = "transaction",
                                        Modifier.size(38.dp),
                                        tint = Color(0xFFDE4251)
                                    )
                                }
                                Spacer(modifier = Modifier.size(12.dp))
                                IconButton(
                                    onClick = {
                                        navController.navigate("reports"){
                                            popUpTo("viewTransactions") { inclusive = true }
                                        }
                                    },
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
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TransactionsPagePreview() {
    TransactionsPage(rememberNavController())
//    Modal()
}