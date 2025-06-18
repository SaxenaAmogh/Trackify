package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.trackify.viewmodel.TransactionViewModel
import com.example.trackify.viewmodel.UtilityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddTransactionPage(navController: NavController, transactionViewModel: TransactionViewModel) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    var friendCount by remember { mutableIntStateOf(0) }
    friendCount = transactionViewModel.selectedFriends.size
    val expenseStatus by transactionViewModel.expenseStatus
    val incomeStatus by transactionViewModel.incomeStatus
    val utilityViewModel: UtilityViewModel = viewModel()
    var expanded by remember { mutableStateOf(false) }
    val payment = listOf("Cash", "Card", "UPI", "Net Banking", "Other")
    var expanded2 by remember { mutableStateOf(false) }
    val category = listOf("Food", "Shopping", "Subscription", "Entertainment", "Travel", "Other")
    var showDatePicker by remember { mutableStateOf(false) }

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val windowInsetsController = window?.let { WindowCompat.getInsetsController(it, view) }

    if (windowInsetsController != null) {
        windowInsetsController.isAppearanceLightStatusBars = false
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerModal(
        onDateSelected: (String) -> Unit, // Now directly returns formatted date string
        onDismiss: () -> Unit
    ) {
        val datePickerState = rememberDatePickerState()

        fun formatDate(millis: Long?): String {
            return if (millis != null) {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = millis
                formatter.format(calendar.time)
            } else {
                ""
            }
        }

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val formattedDate = formatDate(datePickerState.selectedDateMillis)
                    onDateSelected(formattedDate) // Send formatted string directly
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    @SuppressLint("DefaultLocale")
    @Composable
    fun Expense(){
        Text(
            text = "Transaction Title *",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.063 * screenHeight),
            shape = RoundedCornerShape(size = 16.dp),
            placeholder = {
                Text(
                    "Title",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFB2B2B2),
                )
            },
            value = transactionViewModel.title,
            onValueChange = { transactionViewModel.title = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDE4251),
                unfocusedBorderColor = Color(0x66ABABAB),
                focusedTextColor = Color(0xFFFFFFFF),
                unfocusedTextColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0x14ABABAB),
                focusedContainerColor = Color(0x14ABABAB)
            )
        )
        Spacer(modifier = Modifier.height(0.02 * screenHeight))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = "Amount *",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFDE4251),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .height(0.063 * screenHeight),
                    shape = RoundedCornerShape(size = 16.dp),
                    placeholder = {
                        Text(
                            "XXX",
                            fontFamily = latoFontFamily,
                            color = Color(0xFFB2B2B2),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.rupee),
                            contentDescription = "Amount Icon",
                            tint = Color(0xFFB2B2B2),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = transactionViewModel.amount,
                    onValueChange = { transactionViewModel.amount = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
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
            Spacer(modifier = Modifier.width(0.02 * screenWidth))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Payment Type *",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFDE4251),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
                Box {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = transactionViewModel.transactionType,
                            onValueChange = { transactionViewModel.transactionType = it },
                            readOnly = true,
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                            payment.forEach { item ->
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
                                        )
                                    },
                                    onClick = {
                                        transactionViewModel.transactionType = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        Text(
            text = "Date *",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .height(0.063 * screenHeight)
                .background(
                    color = Color(0x14ABABAB),
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (showDatePicker){ Color(0xFFDE4251) } else { Color(0x66ABABAB) },
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    onClick = { showDatePicker = true },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ){
            Text(
                text = transactionViewModel.date.ifEmpty { "dd/mm/yyyy" },
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.CenterStart),
                fontFamily = latoFontFamily,
                color = Color(0xFFFFFFFF),
                fontSize = 16.sp,
            )
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = "Date Icon",
                tint = Color(0xFFB2B2B2),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            )
        }
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { dateString ->
                    // If you want to store full date in viewModel.date:
                    transactionViewModel.date = dateString
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )

        }

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        Text(
            text = "Expense Type *",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded2,
                onExpandedChange = { expanded2 = it }
            ) {
                OutlinedTextField(
                    value = transactionViewModel.category,
                    onValueChange = {transactionViewModel.category = it},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false },
                    modifier = Modifier
                        .background(Color(0xFFFFFFFF)),
                ) {
                    category.forEach { item ->
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
                            text = { Text(
                                item,
                                fontFamily = latoFontFamily,
                            ) },
                            onClick = {
                                transactionViewModel.category = item
                                expanded2 = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        Text(
            text = "Sharing Breakdown",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(0.005 * screenHeight))
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0x66ABABAB),
                    shape = RoundedCornerShape(16.dp
                    )
                )
        ){
            transactionViewModel.selectedFriends.forEach{(friend, contribution)->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            end = 10.dp,
                            top = 0.010 * screenHeight,
                            bottom = 0.010 * screenHeight
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.man),
                        contentDescription = "Transaction Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp)),
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = friend,
                            color = Color.White,
                            fontFamily = latoFontFamily,
                            fontSize = 16.sp,
                        )
                    }
                    Text(
                        text = contribution.toString(),
                        color = Color.White,
                        fontFamily = latoFontFamily,
                        fontSize = 14.sp,
                    )
                }
            }
        }

        if (transactionViewModel.selectedFriends.size != 0) {
            Spacer(modifier = Modifier.height(0.02 * screenHeight))
        }
        FloatingActionButton(
            onClick = {
                if (transactionViewModel.amount == ""){
                    Toast.makeText(
                        context,
                        "Please enter an amount",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    navController.navigate("friends")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0x66ABABAB),
                    shape = RoundedCornerShape(16.dp)
                ),
            containerColor = Color(0x17ABABAB),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp
            )
        ) {
            Text(
                text = if (transactionViewModel.selectedFriends.size == 0) "Add Friends" else "Add/Remove Friends",
                fontFamily = latoFontFamily,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.W500,
            )
        }

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        Text(
            text = "Add Notes",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.15 * screenHeight),
            shape = RoundedCornerShape(size = 16.dp),
            placeholder = {
                Text(
                    "Additional Notes to help you later",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFB2B2B2),
                )
            },
            value = transactionViewModel.note,
            onValueChange = { transactionViewModel.note = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDE4251),
                unfocusedBorderColor = Color(0x66ABABAB),
                focusedTextColor = Color(0xFFFFFFFF),
                unfocusedTextColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0x14ABABAB),
                focusedContainerColor = Color(0x14ABABAB)
            )
        )

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        FloatingActionButton(
            onClick = {
                if(transactionViewModel.title == ""){
                    Toast.makeText(
                        context,
                        "Please enter a title",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (transactionViewModel.amount == ""){
                    Toast.makeText(
                        context,
                        "Please enter an amount",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if( transactionViewModel.amount.contains(" ")){
                    Toast.makeText(
                        context,
                        "Please enter a valid amount without spaces",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (transactionViewModel.transactionType == "Payment") {
                    Toast.makeText(
                        context,
                        "Please select a payment type",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(transactionViewModel.date == "") {
                    Toast.makeText(
                        context,
                        "Please select a date",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(transactionViewModel.category == "Category") {
                    Toast.makeText(
                        context,
                        "Please select a category",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    transactionViewModel.addExpense(
                        title = transactionViewModel.title,
                        amount = transactionViewModel.amount.toDouble(),
                        paymentType = transactionViewModel.transactionType,
                        date = transactionViewModel.date,
                        expenseType = transactionViewModel.category,
                        friends = transactionViewModel.selectedFriends,
                        note = transactionViewModel.note,
                    )
                    transactionViewModel.updateFriendContributions(
                        transactionViewModel.category,
                        transactionViewModel.MyContribution
                    )
                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = Color(0xFFDE4251),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp
            )
        ) {
            Text(
                text = "Save",
                fontFamily = latoFontFamily,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.W500,
            )
        }
        LaunchedEffect(expenseStatus) {
            expenseStatus?.let {
                if (it == "success") {
                    utilityViewModel.subtractFromNetBalance(transactionViewModel.amount.toDouble())
                    transactionViewModel.clearTransaction()
                    Toast.makeText(context, "Expense added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
                transactionViewModel.clearExpenseStatus()
            }
        }
    }

    @Composable
    fun Income(){
        Text(
            text = "Income Title *",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.063 * screenHeight),
            shape = RoundedCornerShape(size = 16.dp),
            placeholder = {
                Text(
                    "Title",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFB2B2B2),
                )
            },
            value = transactionViewModel.title,
            onValueChange = { transactionViewModel.title = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDE4251),
                unfocusedBorderColor = Color(0x66ABABAB),
                focusedTextColor = Color(0xFFFFFFFF),
                unfocusedTextColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0x14ABABAB),
                focusedContainerColor = Color(0x14ABABAB)
            )
        )
        Spacer(modifier = Modifier.height(0.02 * screenHeight))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = "Amount *",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFDE4251),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .height(0.063 * screenHeight),
                    shape = RoundedCornerShape(size = 16.dp),
                    placeholder = {
                        Text(
                            "XXX",
                            fontFamily = latoFontFamily,
                            color = Color(0xFFB2B2B2),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.rupee),
                            contentDescription = "Amount Icon",
                            tint = Color(0xFFB2B2B2),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = transactionViewModel.amount,
                    onValueChange = { transactionViewModel.amount = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
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
            Spacer(modifier = Modifier.width(0.02 * screenWidth))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Payment Type *",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFDE4251),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
                Box {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = transactionViewModel.transactionType,
                            onValueChange = { transactionViewModel.transactionType = it },
                            readOnly = true,
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                            payment.forEach { item ->
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
                                        )
                                    },
                                    onClick = {
                                        transactionViewModel.transactionType = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        Text(
            text = "Date *",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .height(0.063 * screenHeight)
                .background(
                    color = Color(0x14ABABAB),
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (showDatePicker){ Color(0xFFDE4251) } else { Color(0x66ABABAB) },
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    onClick = { showDatePicker = true },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ){
            Text(
                text = transactionViewModel.date.ifEmpty { "dd/mm/yyyy" },
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.CenterStart),
                fontFamily = latoFontFamily,
                color = Color(0xFFFFFFFF),
                fontSize = 16.sp,
            )
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = "Date Icon",
                tint = Color(0xFFB2B2B2),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            )
        }
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { dateString ->
                    // If you want to store full date in viewModel.date:
                    transactionViewModel.date = dateString
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )

        }

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        Text(
            text = "Add Notes",
            fontFamily = latoFontFamily,
            color = Color(0xFFDE4251),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.15 * screenHeight),
            shape = RoundedCornerShape(size = 16.dp),
            placeholder = {
                Text(
                    "Additional Notes to help you later",
                    fontFamily = latoFontFamily,
                    color = Color(0xFFB2B2B2),
                )
            },
            value = transactionViewModel.note,
            onValueChange = { transactionViewModel.note = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDE4251),
                unfocusedBorderColor = Color(0x66ABABAB),
                focusedTextColor = Color(0xFFFFFFFF),
                unfocusedTextColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0x14ABABAB),
                focusedContainerColor = Color(0x14ABABAB)
            )
        )

        Spacer(modifier = Modifier.height(0.02 * screenHeight))
        FloatingActionButton(
            onClick = {
                if(transactionViewModel.title == ""){
                    Toast.makeText(
                        context,
                        "Please enter a title",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (transactionViewModel.amount == ""){
                    Toast.makeText(
                        context,
                        "Please enter an amount",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if( transactionViewModel.amount.contains(" ")){
                    Toast.makeText(
                        context,
                        "Please enter a valid amount without spaces",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (transactionViewModel.transactionType == "Payment") {
                    Toast.makeText(
                        context,
                        "Please select a payment type",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(transactionViewModel.date == "") {
                    Toast.makeText(
                        context,
                        "Please select a date",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    transactionViewModel.addIncome(
                        title = transactionViewModel.title,
                        amount = transactionViewModel.amount.toDouble(),
                        paymentType = transactionViewModel.transactionType,
                        date = transactionViewModel.date,
                        note = transactionViewModel.note,
                    )
                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = Color(0xFFDE4251),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp
            )
        ) {
            Text(
                text = "Save",
                fontFamily = latoFontFamily,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W500,
            )
        }
        LaunchedEffect(incomeStatus) {
            incomeStatus?.let {
                if (it == "success") {
                    utilityViewModel.addToNetBalance(transactionViewModel.amount.toDouble())
                    transactionViewModel.clearTransaction()
                    Toast.makeText(context, "Income added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
                transactionViewModel.clearIncomeStatus()
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
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top+ WindowInsetsSides.Bottom))
                        .padding(
                            horizontal = 0.04 * screenWidth
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ){
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color(0x66ABABAB),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ){
                                Icon(
                                    Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                    contentDescription = "Arrow Icon",
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(32.dp)
                                        .align(Alignment.CenterStart)
                                        .clickable {
                                            navController.popBackStack()
                                        },
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "Add Transaction",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(start = 8.dp),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontFamily = latoFontFamily,
                            )
                        }
                        LazyColumn{
                            item {
                                Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0xFF19191b)
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ){
                                    Button(
                                        onClick = {
                                            transactionViewModel.expense = false
                                            transactionViewModel.clearTransaction()
                                        },
                                        modifier = Modifier
                                            .padding(
                                                vertical = 8.dp,
                                                horizontal = 8.dp
                                            )
                                            .weight(1f)
                                            .height(0.05 * screenHeight),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = if (!transactionViewModel.expense) {
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
                                            text = "Income",
                                            color = if (!transactionViewModel.expense) Color.Black else Color.White,
                                            fontSize = 16.sp,
                                            fontFamily = latoFontFamily,
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            transactionViewModel.expense = true
                                            transactionViewModel.clearTransaction()
                                        },
                                        modifier = Modifier
                                            .padding(
                                                vertical = 8.dp,
                                                horizontal = 8.dp
                                            )
                                            .weight(1f)
                                            .height(0.05 * screenHeight),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = if (transactionViewModel.expense) {
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
                                            text = "Expense",
                                            color = if (transactionViewModel.expense) Color.Black else Color.White,
                                            fontSize = 16.sp,
                                            fontFamily = latoFontFamily,
                                        )
                                    }
                                }
                            }
                            item{
                                Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                if (transactionViewModel.expense) {
                                    Expense()
                                } else {
                                    Income()
                                }

                            }
                        }
                    }

                }
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun TransactionPagePreview() {
    AddTransactionPage(rememberNavController(), viewModel())
}