package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.example.trackify.ui.theme.latoFontFamily
import com.example.trackify.viewmodel.FriendsViewModel
import com.example.trackify.viewmodel.ReportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@Composable
fun FriendDetailsPage(navController: NavController) {

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

    var pending by remember { mutableStateOf(false) }
    var searchItem by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedFriend by remember { mutableStateOf<FriendsViewModel.FriendDetail?>(null) }
    var friendName by remember { mutableStateOf("") }

    val friendsViewModel: FriendsViewModel = viewModel()
    val friendDetails = friendsViewModel.friendDetails.value

    LaunchedEffect(Unit) {
        friendsViewModel.loadFriendDetails()
    }

    val typeImgs = listOf(
        R.drawable.food,
        R.drawable.shopping,
        R.drawable.netflix,
        R.drawable.tv,
        R.drawable.travel
    )


    @Composable
    fun FriendPieChart(data: List<FriendsViewModel.CategoryCount>) {
        val total = data.sumOf { it.count.toDouble() }
        if (total.toFloat() == 0f) {
            Text("No data to display", color = Color.Gray)
            return
        }

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
                .size(250.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                data.forEach { entry ->
                    val sweep = (entry.count / total) * 360f
                    val color = categoryColorMap[entry.category] ?: Color.Gray

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep.toFloat(),
                        useCenter = true
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
                            text = "Friend Details",
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
                                    pending = false
                                },
                                modifier = Modifier
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 8.dp
                                    )
                                    .weight(1f)
                                    .height(0.05 * screenHeight),
                                shape = RoundedCornerShape(16.dp),
                                colors = if (!pending) {
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
                                    text = "Friends",
                                    color = if (!pending) Color.Black else Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = latoFontFamily,
                                )
                            }
                            Button(
                                onClick = {
                                    pending = true
                                },
                                modifier = Modifier
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 8.dp
                                    )
                                    .weight(1f)
                                    .height(0.05 * screenHeight),
                                shape = RoundedCornerShape(16.dp),
                                colors = if (pending) {
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
                                    text = "Pending Status",
                                    color = if (pending) Color.Black else Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = latoFontFamily,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(0.02 * screenHeight))
                        LazyColumn {
                            item{
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                            item{
                                Spacer(modifier = Modifier.height(0.02 * screenHeight))
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
                                        .heightIn(
                                            min = 0.2 * screenHeight,
                                        )
                                ){
                                    friendDetails.entries.toList().forEach{(name, detail) ->
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
                                                selectedFriend = detail
                                                showSheet = true
                                                friendName  = name
                                                },
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Image(
                                                painter = painterResource(R.drawable.man_2), // adjust as needed
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
                                                    text = name,
                                                    color = Color.White,
                                                    fontFamily = latoFontFamily,
                                                    fontSize = 17.sp,
                                                )
                                                Text(
                                                    text = " ₹${String.format("%.2f", detail.pending)}",
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
                                                    },
                                                tint = Color.White
                                            )
                                        }
                                        Divider(
                                            color = Color(0x66ABABAB),
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(horizontal = 10.dp)
                                        )
                                    }
                                }

                                if (showSheet) {
                                    val chartData = friendsViewModel.convertCategoryMapToChartData(selectedFriend)

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
                                                .height(0.85 * screenHeight),
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
                                                    .background(Color.Transparent),
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
                                                            painter = painterResource(R.drawable.man_2),
                                                            contentDescription = "shopping",
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .size(0.11 * screenHeight)
                                                                .padding(
                                                                    start = 8.dp,
                                                                    end = 4.dp,
                                                                    top = 5.dp,
                                                                    bottom = 5.dp
                                                                ),
                                                        )
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = friendName,
                                                            color = Color.White,
                                                            fontSize = 28.sp,
                                                            fontFamily = latoFontFamily,
                                                            fontWeight = FontWeight.Bold,
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
                                                        val categoryMax = selectedFriend?.category?.maxByOrNull { it.value }?.key ?: "No data"
                                                        Text(
                                                            text = "Most Spent on:",
                                                            color = Color(0xFFADADAD),
                                                            fontFamily = latoFontFamily,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.W500,
                                                            textDecoration = TextDecoration.Underline,
                                                        )
                                                        Text(
                                                            text = categoryMax,
                                                            color = Color.White,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.W500,
                                                            fontFamily = latoFontFamily,
                                                            modifier = Modifier.padding(start = 6.dp)
                                                        )
                                                        Image(
                                                            painter = painterResource(
                                                                if (categoryMax == "Food") typeImgs[0]
                                                                else if (categoryMax == "Shopping") typeImgs[1]
                                                                else if (categoryMax == "Subscription") typeImgs[2]
                                                                else if (categoryMax == "Entertainment") typeImgs[3]
                                                                else if (categoryMax == "Travel") typeImgs[4]
                                                                else R.drawable.others
                                                            ),
                                                            contentDescription = "shopping",
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .size(0.07 * screenHeight)
                                                                .padding(
                                                                    start = 8.dp,
                                                                    end = 4.dp,
                                                                    top = 5.dp,
                                                                    bottom = 5.dp
                                                                ),
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
                                                            text = "Pending Balance:",
                                                            color = Color(0xFFADADAD),
                                                            fontFamily = latoFontFamily,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.W500,
                                                            textDecoration = TextDecoration.Underline,
                                                        )
                                                        Text(
                                                            text = " ₹${selectedFriend?.let { it1 -> String.format("%.2f", it1.pending) }}",
                                                            color = Color.White,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.W500,
                                                            fontFamily = latoFontFamily,
                                                            modifier = Modifier.padding(start = 6.dp)
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
                                                            text = "I've spent with $friendName:",
                                                            color = Color(0xFFADADAD),
                                                            fontFamily = latoFontFamily,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.W500,
                                                            textDecoration = TextDecoration.Underline,
                                                        )
                                                        Text(
                                                            text = " ₹${selectedFriend?.let { it1 -> String.format("%.2f", it1.mySpending) }}",
                                                            color = Color.White,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.W500,
                                                            fontFamily = latoFontFamily,
                                                            modifier = Modifier.padding(start = 6.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ){
                                                        FriendPieChart(chartData)

                                                        Spacer(modifier = Modifier.height(5.dp))

                                                        val categoryColorMap = mapOf(
                                                            "Food" to Color(0xFFDE4251),
                                                            "Shopping" to Color(0xFFFF9800),
                                                            "Entertainment" to Color(0xFF3F51B5),
                                                            "Subscriptions" to Color(0xFF009688),
                                                            "Travel" to Color(0xFF9C27B0),
                                                            "Other" to Color(0xFF607D8B)
                                                        )

                                                        chartData.forEach { (category, count) ->
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
                                                                    text = count.toString(),
                                                                    color = Color(0xFFFFFFFF),
                                                                    fontSize = 18.sp,
                                                                    fontFamily = latoFontFamily,
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (showSheet) Color(0xFF000000).copy(alpha = 0.93f) else Color.Transparent
                            )
                    ){}


                    // Bottom Navigation Bar
                    if (!showSheet) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    start = 0.045 * screenWidth,
                                    end = 0.045 * screenWidth,
                                    bottom = 0.045 * screenHeight
                                )
                                .align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                    onClick = {
                                        navController.navigate("reports") {
                                            popUpTo("friendDetails") { inclusive = true }
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
                                        .padding(end = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.friendsicon),
                                        contentDescription = "user",
                                        Modifier.size(32.dp),
                                        tint = Color(0xFFDE4251)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
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

@Preview(showBackground = true)
@Composable
fun FriendDetailsPagePreview() {
    FriendDetailsPage(rememberNavController())
}
