package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Paint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.trackify.viewmodel.ReportsViewModel
import com.example.trackify.viewmodel.UtilityViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReportsPage(navController: NavController) {

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

    var expanded by remember { mutableStateOf(false) }
    val months = listOf("This Month", "This Week", "Last Month", "This Year")
    var selectedOption by remember { mutableStateOf("This Month") }

    var expanded2 by remember { mutableStateOf(false) }
    val months2 = listOf("This Month", "This Week", "Last Month", "This Year")
    var selectedOption2 by remember { mutableStateOf("This Month") }

    var expanded3 by remember { mutableStateOf(false) }
    val months3 = listOf("This Month", "This Week", "Last Month", "This Year")
    var selectedOption3 by remember { mutableStateOf("This Month") }

    var analytics by remember { mutableStateOf(true) }

    val utilityViewModel: UtilityViewModel = viewModel()
    utilityViewModel.fetchAllIncomes()
    utilityViewModel.fetchAllRawExpenses()
    val allExpensesRaw by utilityViewModel.allExpensesRaw.collectAsState()
    val allIncomes by utilityViewModel.allIncomes.collectAsState()
    val reportsViewModel: ReportsViewModel = viewModel()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val totalWithFriends = utilityViewModel.getCurrentMonthExpensesFriends(allExpensesRaw, formatter)

    var incomeTotal by remember { mutableDoubleStateOf(0.0) }
    var expenseTotal by remember { mutableDoubleStateOf(0.0) }
    val (income, expense) = reportsViewModel.getFilteredIncomeAndExpenseTotal(selectedOption, allExpensesRaw, allIncomes)
    incomeTotal = income
    expenseTotal = expense

    var expenseTotal2 by remember { mutableDoubleStateOf(0.0) }
    val (income2, expense2) = reportsViewModel.getFilteredIncomeAndExpenseTotal(selectedOption2, allExpensesRaw, allIncomes)
    expenseTotal2 = expense2

    val categoryStats = remember(allExpensesRaw, selectedOption2) {
        reportsViewModel.getCategoryCounts(allExpensesRaw, selectedOption2)
    }
    val categoryCounts = categoryStats.counts           // List<CategoryCount>
    val categoryAmounts = categoryStats.amounts         // Map<String, Double>

    val payModeData = remember(allExpensesRaw, selectedOption3) {
        reportsViewModel.getPayModeCounts(allExpensesRaw, selectedOption3)
    }

    var seeLess by remember { mutableStateOf(true) }
    val allFriendsSpent = reportsViewModel.calculateTotalSpentPerFriend(allExpensesRaw)

    val weeklyData = remember(allIncomes, allExpensesRaw) {
        reportsViewModel.calculateWeeklyTotalsThisMonth(allIncomes, allExpensesRaw)
    }

//    val authViewModel: AuthViewModel = viewModel()

    @Composable
    fun WeeklyLineChart(data: List<ReportsViewModel.WeeklyTotals>) {
        val pointRadius = with(LocalDensity.current) { 6.dp.toPx() }
        val incomeCol = Color(0xFF00BCD4)
        val expenseColor = Color(0xFFDE4251)
        val steps = 5

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.3f * screenHeight)
                .background(Color.Transparent)
                .padding(
                    start = 25.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp
                )
        ) {
            val width = size.width
            val height = size.height - 60f
            val topPadding = 20f
            val spacing = width / data.size

            val allValues = data.flatMap { listOf(it.income, it.expense) }
            val minY = allValues.minOrNull() ?: 0f
            val maxRaw = allValues.maxOrNull() ?: 0f

            // Interval logic based on range
            val range = maxRaw - minY
            val interval = when {
                range <= 100 -> 20
                range <= 500 -> 50
                range <= 1000 -> 100
                range <= 2000 -> 200
                range <= 5000 -> 500
                else -> 1000
            }

            val axisMax = (((maxRaw + 200f) / interval).toInt() + 1) * interval
            val axisMin = 0
            val axisRange = (axisMax - axisMin).toFloat()

            // Draw horizontal lines and labels
            repeat(steps + 1) { i ->
                val y = topPadding + (height / steps) * i
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(80f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    (axisMax - i * (axisMax / steps)).toString(),
                    -15f,
                    y - 4f,
                    Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 38f
                    }
                )
            }

            // Line plot function
            fun drawLinePath(values: List<Float>, color: Color) {
                val path = Path()
                values.forEachIndexed { index, value ->
                    val x = (spacing * index + spacing / 2) + 20f
                    val y = topPadding + height * (1 - (value / axisRange))
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    drawCircle(color, pointRadius, Offset(x, y))
                }
                drawPath(path, color = color, style = Stroke(width = 3.dp.toPx()))
            }

            // Draw income and expense lines
            drawLinePath(data.map { it.income }, incomeCol)
            drawLinePath(data.map { it.expense }, expenseColor)

            // X-axis labels (weeks)
            data.forEachIndexed { index, it ->
                val x = spacing * index + spacing / 2
                drawContext.canvas.nativeCanvas.drawText(
                    it.week,
                    x - 35f,
                    size.height + 20f,
                    Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 35f
                    }
                )
            }
        }
    }

    @Composable
    fun ExpenseRingChart(data: List<ReportsViewModel.CategoryCount>) {
        val total = data.sumOf { it.count.toDouble() }

        val categoryColorMap = mapOf(
            "Food" to Color(0xFFDE4251),
            "Shopping" to Color(0xFFFF9800),
            "Entertainment" to Color(0xFF3F51B5),
            "Subscriptions" to Color(0xFF009688),
            "Travel" to Color(0xFF9C27B0),
            "Other" to Color(0xFF607D8B)
        )


        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                data.forEach { entry ->
                    val sweep = (entry.count.toDouble() / total) * 360f
                    val color = categoryColorMap[entry.category] ?: Color.Gray // Fallback in case of mismatch

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep.toFloat(),
                        useCenter = false,
                        style = Stroke(width = 80f, cap = StrokeCap.Butt)
                    )
                    startAngle += sweep.toFloat()
                }

            }

            // Center text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Total Expenses",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = " ₹${String.format("%.2f", expenseTotal2)}",
                    color = Color(0xFFDE4251),
                    fontSize = 28.sp,
                    fontFamily = latoFontFamily,
                )
            }
        }
    }

    @Composable
    fun PaymentModeChart(data: List<ReportsViewModel.PayModeCount>) {
        val total = data.sumOf { it.count.toDouble() }

        val categoryColorMap = mapOf(
            "UPI" to Color(0xFFDE4251),
            "Cash" to Color(0xFF00BCD4),
            "Card" to Color(0xFFFF9800),
            "Net Banking" to Color(0xFF3F51B5),
            "Other" to Color(0xFF607D8B)
        )


        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                data.forEach { entry ->
                    val sweep = (entry.count.toDouble() / total) * 360f
                    val color = categoryColorMap[entry.paymentType] ?: Color.Gray

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep.toFloat(),
                        useCenter = true // Makes it a filled slice (solid pie)
                        // No stroke here
                    )
                    startAngle += sweep.toFloat()
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
                        Text(
                            text = "Transaction Statistics",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 0.02 * screenHeight),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontFamily = latoFontFamily,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF19191b)
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    analytics = true
                                },
                                modifier = Modifier
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 8.dp
                                    )
                                    .weight(1f)
                                    .height(0.05 * screenHeight),
                                shape = RoundedCornerShape(16.dp),
                                colors = if (analytics) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDE4251)
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    )
                                }
                            ) {
                                Text(
                                    text = "Analytics",
                                    color = if (analytics) Color.Black else Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = latoFontFamily,
                                )
                            }
                            Button(
                                onClick = {
                                    analytics = false
                                },
                                modifier = Modifier
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 8.dp
                                    )
                                    .weight(1f)
                                    .height(0.05 * screenHeight),
                                shape = RoundedCornerShape(16.dp),
                                colors = if (!analytics) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDE4251)
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    )
                                }
                            ) {
                                Text(
                                    text = "Transactions",
                                    color = if (!analytics) Color.Black else Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = latoFontFamily,
                                )
                            }
                        }
                        if (analytics){
                            LazyColumn {
                                // Analytics Section
                                item {
                                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
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
                                                .padding(
                                                    top = 16.dp
                                                ),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "This Week's Analytics",
                                                color = Color.White,
                                                fontSize = 22.sp,
                                                fontFamily = latoFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(){
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .background(Color(0xFF00BCD4), shape = RoundedCornerShape(18))
                                                    ){}
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Text(
                                                        text = "Income",
                                                        color = Color(0xFFFFFFFF),
                                                        fontSize = 16.sp,
                                                        fontFamily = latoFontFamily,
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .background(Color(0xFFDE4251), shape = RoundedCornerShape(18))
                                                    ){}
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Text(
                                                        text = "Expense",
                                                        color = Color(0xFFFFFFFF),
                                                        fontSize = 16.sp,
                                                        fontFamily = latoFontFamily,
                                                    )
                                                }
                                            }
                                        }
                                        WeeklyLineChart(weeklyData)
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
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
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Most Spent On $selectedOption2",
                                                color = Color.White,
                                                fontSize = 22.sp,
                                                fontFamily = latoFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            ExposedDropdownMenuBox(
                                                expanded = expanded2,
                                                onExpandedChange = { expanded2 = it }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .menuAnchor()
                                                        .border(
                                                            width = 0.5.dp,
                                                            color = Color(0xFFFFFFFF),
                                                            shape = RoundedCornerShape(50.dp)
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ){
                                                    Text(
                                                        text = selectedOption2,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontFamily = latoFontFamily,
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 12.dp,
                                                                top = 6.dp,
                                                                bottom = 6.dp
                                                            )
                                                    )

                                                    Icon(
                                                        Icons.Default.ArrowDropDown,
                                                        contentDescription = "Dropdown",
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .padding(end = 6.dp)
                                                            .clickable {
                                                                expanded2 = true
                                                            }
                                                            .align(Alignment.CenterVertically)
                                                    )
                                                }

                                                ExposedDropdownMenu(
                                                    expanded = expanded2,
                                                    onDismissRequest = { expanded2 = false },
                                                    modifier = Modifier
                                                        .background(Color(0xFFFFFFFF)),
                                                ) {
                                                    months2.forEach { item ->
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
                                                                selectedOption2 = item
                                                                val (incomes2, expenses2) = reportsViewModel.getFilteredIncomeAndExpenseTotal(selectedOption2, allExpensesRaw, allIncomes)
                                                                expenseTotal2 = expenses2
                                                                expanded2 = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))

                                        if (categoryAmounts.isEmpty()){
                                            Text(
                                                text = "No data available for $selectedOption3",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontFamily = latoFontFamily,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }else {
                                            ExpenseRingChart(categoryCounts)

                                            Spacer(modifier = Modifier.height(5.dp))

                                            val categoryColorMap = mapOf(
                                                "Food" to Color(0xFFDE4251),
                                                "Shopping" to Color(0xFFFF9800),
                                                "Entertainment" to Color(0xFF3F51B5),
                                                "Subscriptions" to Color(0xFF009688),
                                                "Travel" to Color(0xFF9C27B0),
                                                "Other" to Color(0xFF607D8B)
                                            )

                                            categoryAmounts.forEach { (category, amount) ->
                                                val color = categoryColorMap[category] ?: Color.Gray
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            horizontal = 24.dp
                                                        ),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .background(
                                                                    color,
                                                                    shape = RoundedCornerShape(18)
                                                                )
                                                        ) {}
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = category,
                                                            color = Color(0xFFFFFFFF),
                                                            fontSize = 18.sp,
                                                            fontFamily = latoFontFamily,
                                                        )
                                                    }
                                                    Text(
                                                        text = "₹${String.format("%.2f", amount)}",
                                                        color = Color(0xFFFFFFFF),
                                                        fontSize = 18.sp,
                                                        fontFamily = latoFontFamily,
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
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
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Most Used Pay Mode",
                                                color = Color.White,
                                                fontSize = 22.sp,
                                                fontFamily = latoFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            ExposedDropdownMenuBox(
                                                expanded = expanded3,
                                                onExpandedChange = { expanded3 = it }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .menuAnchor()
                                                        .border(
                                                            width = 0.5.dp,
                                                            color = Color(0xFFFFFFFF),
                                                            shape = RoundedCornerShape(50.dp)
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ){
                                                    Text(
                                                        text = selectedOption3,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontFamily = latoFontFamily,
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 12.dp,
                                                                top = 6.dp,
                                                                bottom = 6.dp
                                                            )
                                                    )

                                                    Icon(
                                                        Icons.Default.ArrowDropDown,
                                                        contentDescription = "Dropdown",
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .padding(end = 6.dp)
                                                            .clickable {
                                                                expanded3 = true
                                                            }
                                                            .align(Alignment.CenterVertically)
                                                    )
                                                }

                                                ExposedDropdownMenu(
                                                    expanded = expanded3,
                                                    onDismissRequest = { expanded3 = false },
                                                    modifier = Modifier
                                                        .background(Color(0xFFFFFFFF)),
                                                ) {
                                                    months3.forEach { item ->
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
                                                                selectedOption3 = item
                                                                expanded3 = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))

                                        if (payModeData.isEmpty()){
                                            Text(
                                                text = "No data available for $selectedOption3",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontFamily = latoFontFamily,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        } else {
                                            PaymentModeChart(payModeData)

                                            Spacer(modifier = Modifier.height(5.dp))

                                            val categoryColorMaps = mapOf(
                                                "UPI" to Color(0xFFDE4251),
                                                "Cash" to Color(0xFF00BCD4),
                                                "Card" to Color(0xFFFF9800),
                                                "Net Banking" to Color(0xFF3F51B5),
                                                "Other" to Color(0xFF607D8B)
                                            )

                                            payModeData.forEach{ (payMode, count) ->
                                                val color = categoryColorMaps[payMode] ?: Color.Gray
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            horizontal = 50.dp
                                                        ),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ){
                                                        Box(
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .background(
                                                                    color,
                                                                    shape = RoundedCornerShape(18)
                                                                )
                                                        ){}
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = payMode,
                                                            color = Color(0xFFFFFFFF),
                                                            fontSize = 18.sp,
                                                            fontFamily = latoFontFamily,
                                                        )
                                                    }
                                                    Text(
                                                        text = "$count times",
                                                        color = Color(0xFFFFFFFF),
                                                        fontSize = 18.sp,
                                                        fontFamily = latoFontFamily,
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                    Spacer(modifier = Modifier.height(0.16 * screenHeight))
                                }
                            }
                        }else {
                            LazyColumn {
                                //transactions section
                                item {
                                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
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
                                            Spacer(modifier = Modifier.height(10.dp))
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = { expanded = it }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .menuAnchor()
                                                        .border(
                                                            width = 0.5.dp,
                                                            color = Color(0xFFFFFFFF),
                                                            shape = RoundedCornerShape(50.dp)
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = selectedOption,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontFamily = latoFontFamily,
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 12.dp,
                                                                top = 6.dp,
                                                                bottom = 6.dp
                                                            )
                                                    )

                                                    Icon(
                                                        Icons.Default.ArrowDropDown,
                                                        contentDescription = "Dropdown",
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .padding(end = 6.dp)
                                                            .clickable {
                                                                expanded = true
                                                            }
                                                            .align(Alignment.CenterVertically)
                                                    )
                                                }

                                                ExposedDropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = { expanded = false },
                                                    modifier = Modifier
                                                        .background(Color(0xFFFFFFFF)),
                                                ) {
                                                    months.forEach { item ->
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
                                                                selectedOption = item
                                                                val (incomes, expenses) = reportsViewModel.getFilteredIncomeAndExpenseTotal(
                                                                    selectedOption,
                                                                    allExpensesRaw,
                                                                    allIncomes
                                                                )
                                                                incomeTotal = incomes
                                                                expenseTotal = expenses
                                                                expanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
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
                                }
                                item {
                                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
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
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Most Spent With",
                                                color = Color.White,
                                                fontSize = 22.sp,
                                                fontFamily = latoFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Total spent this month:  ",
                                                    color = Color.White,
                                                    fontSize = 16.sp,
                                                    fontFamily = latoFontFamily,
                                                )
                                                Text(
                                                    text = if (totalWithFriends == null || totalWithFriends == 0.0) "Loading..." else "₹${String.format("%.2f",totalWithFriends)
                                                    }",
                                                    color = Color(0xFFDE4251),
                                                    fontSize = 20.sp,
                                                    fontFamily = latoFontFamily,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 10.dp,
                                                    end = 10.dp,
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Name",
                                                color = Color(0xFFDE4251),
                                                modifier = Modifier
                                                    .weight(0.5f),
                                                fontFamily = latoFontFamily,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = "Spent",
                                                color = Color(0xFFDE4251),
                                                modifier = Modifier
                                                    .weight(0.5f),
                                                fontFamily = latoFontFamily,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        if (seeLess) {
                                            val top4Friends = allFriendsSpent.entries.take(3)
                                            top4Friends.forEach { (name, amount) ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            start = 10.dp,
                                                            end = 10.dp,
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = name,
                                                        color = Color(0xFFFFFFFF),
                                                        modifier = Modifier
                                                            .weight(0.5f),
                                                        fontFamily = latoFontFamily,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
                                                        text = "₹${String.format("%.2f", amount)}",
                                                        color = Color(0xFFFFFFFF),
                                                        modifier = Modifier
                                                            .weight(0.5f),
                                                        fontFamily = latoFontFamily,
                                                        fontSize = 18.sp,
                                                        textAlign = TextAlign.Center,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        } else {
                                            allFriendsSpent.forEach { (name, amount) ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            start = 10.dp,
                                                            end = 10.dp,
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = name,
                                                        color = Color(0xFFFFFFFF),
                                                        modifier = Modifier
                                                            .weight(0.5f),
                                                        fontFamily = latoFontFamily,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
                                                        text = "₹${String.format("%.2f", amount)}",
                                                        color = Color(0xFFFFFFFF),
                                                        modifier = Modifier
                                                            .weight(0.5f),
                                                        fontFamily = latoFontFamily,
                                                        fontSize = 18.sp,
                                                        textAlign = TextAlign.Center,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                        Text(
                                            text = if (seeLess) "See More" else "See Less",
                                            color = Color(0xFFDE4251),
                                            fontSize = 16.sp,
                                            fontFamily = latoFontFamily,
                                            modifier = Modifier
                                                .padding(
                                                    vertical = 8.dp
                                                )
                                                .clickable {
                                                    seeLess = !seeLess
                                                }
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }


                    // Bottom Navigation Bar
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
                                onClick = {
                                    navController.navigate("viewTransactions") {
                                        popUpTo("reports") { inclusive = true }
                                    }
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .size(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.transaction_nn),
                                    contentDescription = "transaction",
                                    Modifier.size(38.dp),
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
                                    painter = painterResource(R.drawable.report),
                                    contentDescription = "reports",
                                    Modifier.size(30.dp),
                                    tint = Color(0xFFDE4251)
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ReportsPagePreview() {
    ReportsPage(rememberNavController())
}