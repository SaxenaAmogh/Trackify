package com.example.trackify.view

import android.annotation.SuppressLint
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trackify.R
import com.example.trackify.ui.theme.latoFontFamily
import com.example.trackify.viewmodel.TransactionViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddFriendsPage(navController: NavController, transactionViewModel: TransactionViewModel) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var friendCount by remember { mutableIntStateOf(0) }
    friendCount = transactionViewModel.selectedFriends.size
    var amount by remember { mutableStateOf("") }
    var equalSplit by remember { mutableStateOf(false) }
    var paid by remember { mutableStateOf(false) }
    var split by remember { mutableStateOf(false) }
    var friends by remember { mutableStateOf("")}
    val focusManager = LocalFocusManager.current

    @Composable
    fun CustomInputDialog(
        onDismiss: () -> Unit,
        onConfirm: (String, Boolean, Boolean, String) -> Unit
    ) {
        // State variables for inputs
        var name by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                color = Color(0xFF151515),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF1A1A1A),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Add Friend",
                        fontFamily = latoFontFamily,
                        color = Color(0xFFDE4251),
                        fontSize = 18.sp,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.063 * screenHeight),
                        shape = RoundedCornerShape(size = 16.dp),
                        placeholder = {
                            Text(
                                "Friend Name",
                                fontFamily = latoFontFamily,
                                color = Color(0xFFB2B2B2),
                            )
                        },
                        value = name,
                        onValueChange = { name = it },
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

                    if(split){
                        amount = (transactionViewModel.amount.toFloatOrNull()?.div(friends.toIntOrNull()
                            ?.plus(1) ?: 1)?.toString() ?: "")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(0.8f)
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
                            value = amount,
                            onValueChange = { amount = it },
                            singleLine = true,
                            readOnly = split,
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

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(
                            modifier = Modifier
                                .weight(0.3f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Paid",
                                color = Color(0xFFFFFFFF),
                                fontFamily = latoFontFamily,
                                fontSize = 18.sp,
                            )
                            Checkbox(
                                checked = paid,
                                onCheckedChange = { paid = it },
                                colors = androidx.compose.material3.CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFDE4251),
                                    uncheckedColor = Color(0xFFB2B2B2),
                                    checkmarkColor = Color.White,
                                    disabledCheckedColor = Color(0xFFDE4251).copy(alpha = 0.5f),
                                    disabledUncheckedColor = Color(0xFFB2B2B2).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                "Cancel",
                                color = Color(0xFFDE4251),
                                fontFamily = latoFontFamily,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            // Now pass all values back to parent composable
                            onConfirm(name, equalSplit, paid, amount)
                            onDismiss()
                        }) {
                            Text(
                                "Add",
                                color = Color(0xFFFFFFFF),
                                fontFamily = latoFontFamily,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Bottom))
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
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                    contentDescription = "Arrow Icon",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .align(Alignment.CenterStart)
                                        .clickable {
                                            transactionViewModel.clearSelectedFriends()
                                            navController.popBackStack()
                                        },
                                    tint = Color.White
                                )
                                Text(
                                    text = "Add Friends",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(start = 8.dp),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontFamily = latoFontFamily,
                                )
                            }
                        }
                        item{
                            Spacer(modifier = Modifier.size(0.02 * screenHeight))
                            Column(
                                modifier = Modifier
                                    .background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.4.dp,
                                        color = Color(0x66ABABAB),
                                        shape = RoundedCornerShape(16.dp
                                        )
                                    )
                            ){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            top = 0.020 * screenHeight,
                                            bottom = 0.010 * screenHeight
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "",
                                        color = Color.White,
                                        modifier = Modifier
                                            .weight(0.08f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "Name",
                                        color = Color(0xFFDE4251),
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .weight(0.18f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Share",
                                        color = Color(0xFFDE4251),
                                        modifier = Modifier
                                            .weight(0.1f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Paid",
                                        color = Color(0xFFDE4251),
                                        modifier = Modifier
                                            .weight(0.1f),
                                        fontFamily = latoFontFamily,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "",
                                        modifier = Modifier
                                            .weight(0.05f),
                                    )
                                }
                                repeat(friendCount){
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
                                        Image(
                                            painter = painterResource(R.drawable.man),
                                            contentDescription = "Transaction Icon",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .weight(0.08f)
                                                .clip(RoundedCornerShape(50.dp)),
                                        )
                                        Text(
                                            text = transactionViewModel.selectedFriends[it].name,
                                            color = Color.White,
                                            modifier = Modifier
                                                .padding(start = 12.dp)
                                                .weight(0.18f),
                                            fontFamily = latoFontFamily,
                                            fontSize = 18.sp,
                                        )
                                        Text(
                                            text = transactionViewModel.selectedFriends[it].contribution.toString(),
                                            color = Color.White,
                                            modifier = Modifier
                                                .weight(0.1f),
                                            fontFamily = latoFontFamily,
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = transactionViewModel.selectedFriends[it].paid.toString(),
                                            color = Color.White,
                                            modifier = Modifier
                                                .weight(0.1f),
                                            fontFamily = latoFontFamily,
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Icon(
                                            Icons.Rounded.Clear,
                                            contentDescription = "Arrow Icon",
                                            modifier = Modifier
                                                .size(26.dp)
                                                .weight(0.05f)
                                                .clickable {
                                                    navController.popBackStack()
                                                },
                                            tint = Color.White
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            top = 12.dp
                                        ),
                                    color = Color(0xFFDE4251),
                                    thickness = 1.dp
                                )
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = 22.dp
                                        ),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(
                                        text = "Total",
                                        color = Color(0xFFDE4251),
                                        fontFamily = latoFontFamily,
                                        fontSize = 22.sp,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "₹ " + transactionViewModel.amount,
                                        color = Color.White,
                                        fontFamily = latoFontFamily,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        item{
                            Spacer(modifier = Modifier.height(0.03 * screenHeight))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .weight(0.45f)
                                        .height(0.063 * screenHeight),
                                    shape = RoundedCornerShape(size = 16.dp),
                                    placeholder = {
                                        Text(
                                            "Friend Count",
                                            fontFamily = latoFontFamily,
                                            color = Color(0xFFB2B2B2),
                                        )
                                    },
                                    value = friends,
                                    onValueChange = { friends = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus() // Hide the keyboard
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
                                Row(
                                    modifier = Modifier
                                        .weight(0.45f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        "Equally Split",
                                        color = Color(0xFFFFFFFF),
                                        fontFamily = latoFontFamily,
                                        fontSize = 18.sp,
                                    )
                                    Switch(
                                        checked = equalSplit,
                                        onCheckedChange = {
                                            focusManager.clearFocus() // Hide the keyboard
                                            equalSplit = it
                                            split = it
                                            amount = if (split)"" else amount
                                        },
                                        colors = SwitchColors(
                                            checkedThumbColor = Color(0xFFDE4251),
                                            uncheckedThumbColor = Color(0xFFB2B2B2),
                                            checkedTrackColor = Color(0xFFDE4251).copy(alpha = 0.5f),
                                            uncheckedTrackColor = Color(0xFFB2B2B2).copy(alpha = 0.5f),
                                            checkedBorderColor = Color.Transparent,
                                            checkedIconColor = Color.Transparent,
                                            uncheckedBorderColor = Color.Transparent,
                                            uncheckedIconColor = Color.Transparent,
                                            disabledCheckedThumbColor = Color.Transparent,
                                            disabledCheckedTrackColor = Color.Transparent,
                                            disabledCheckedBorderColor = Color.Transparent,
                                            disabledCheckedIconColor = Color.Transparent,
                                            disabledUncheckedThumbColor = Color.Transparent,
                                            disabledUncheckedTrackColor = Color.Transparent,
                                            disabledUncheckedBorderColor = Color.Transparent,
                                            disabledUncheckedIconColor = Color.Transparent,
                                        )
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            FloatingActionButton(
                                onClick = {
                                    focusManager.clearFocus() // Hide the keyboard
                                    if (friends == "") {
                                        Toast.makeText(
                                            context,
                                            "Please enter the number of friends, excluding yourself",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }else if (friendCount< friends.toIntOrNull()!!){
                                        showDialog = true
                                    }else{
                                        Toast.makeText(
                                            context,
                                            "You have already added $friendCount friends",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                    text = "Edit Friends",
                                    fontFamily = latoFontFamily,
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            FloatingActionButton(
                                onClick = {
                                    if (friendCount == friends.toIntOrNull() || friends == "") {
                                        navController.popBackStack()
                                    }else{
                                        Toast.makeText(
                                            context,
                                            "Please add all friends before proceeding",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                    text = "Done",
                                    fontFamily = latoFontFamily,
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }

                            if (showDialog) {
                                CustomInputDialog(
                                    onDismiss = { showDialog = false },
                                    onConfirm = { name, _, paid, amount ->
                                        // Handle the input values here
                                        transactionViewModel.addFriendContribution(name, amount.toFloatOrNull() ?: 0f, paid)
                                    }
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
fun AddFriendsPagePreview() {
    AddFriendsPage(rememberNavController(), viewModel())
}