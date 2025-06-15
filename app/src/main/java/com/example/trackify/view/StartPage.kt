package com.example.trackify.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trackify.R
import com.example.trackify.ui.theme.latoFontFamily

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StartPage(navController: NavController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val windowInsetsController = window?.let { WindowCompat.getInsetsController(it, view) }

    if (windowInsetsController != null) {
        windowInsetsController.isAppearanceLightStatusBars = false
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xEDC2404D), // darker red (you can adjust)
                                Color(0xF0BE3B48),  // lighter red (you can adjust)
                                Color(0xFFC93043), // starting color
                            )
                        )
                    )
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 0.04 * screenWidth
                        )
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                ) {
                    Image(
                        painter = painterResource(R.drawable.start),
                        contentDescription = "Trackify",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .height(0.8 * screenHeight)
                            .padding(
                                top = 0.05 * screenHeight,
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                            .padding(
                                bottom = 0.04 * screenWidth
                            )
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xC1C2404D),
                                        Color(0xFFC93043),
                                        Color(0xFFC93043),
                                        Color(0xFFC93043), // darker red (you can adjust)
                                        Color(0xF0BE3B48),  // lighter red (you can adjust)
                                        Color(0xFFC93043), // starting color
                                    )
                                )
                            )

                    ){
                        Spacer(modifier = Modifier.height(0.08 * screenHeight))
                        Text(
                            text = "Track Your Expenses, Stay in Control",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontFamily = latoFontFamily,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
                        )
                        Spacer( modifier = Modifier.height(8.dp))
                        Text(
                            text = "Track your income and expenses with Trackify. Gain insights into your spending habits.",
                            color = Color(0xFFD9D9D9),
                            textAlign = TextAlign.Center,
                            fontFamily = latoFontFamily,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 2.dp
                                ),
                        )
                        Spacer(modifier = Modifier.height(0.05 * screenHeight))
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("signup")
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            containerColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                focusedElevation = 0.dp,
                                hoveredElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "Get Started",
                                fontFamily = latoFontFamily,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W500,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("login")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF000000),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            containerColor = Color(0xFFC93043),
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                focusedElevation = 0.dp,
                                hoveredElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "Sign in",
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
    )
}


@Preview(showBackground = true)
@Composable
fun StartPagePreview() {
    StartPage(rememberNavController())
}
