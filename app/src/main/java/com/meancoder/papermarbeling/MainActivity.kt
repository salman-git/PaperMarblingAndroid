package com.meancoder.papermarbeling

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.meancoder.papermarbeling.ui.theme.PaperMarbelingTheme

class MainActivity : ComponentActivity() {
    private val TAG: String = "MainActivity"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContent {
            PaperMarbelingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val customSurfaceView by remember {
                        mutableStateOf(CustomSurfaceView(this)) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(factory = { context ->
                            customSurfaceView
                        })

                        Row( modifier = Modifier
                            //left menu
                            .align(Alignment.CenterStart)
                        ) {
                            var isLeftMenuVisible by remember { mutableStateOf(false) }
                            AnimatedVisibility(
                                visible = isLeftMenuVisible,
                                enter = slideInHorizontally(),
                                exit = slideOutHorizontally()
                            ) {
                                LeftMenu(customSurfaceView)
                            }
                            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                // Left menu Hide button
                                Button(
                                    onClick = { isLeftMenuVisible = !isLeftMenuVisible},
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .width(24.dp)
                                        .background(
                                            Color.Black.copy(0.3f),
                                            RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                        ),
                                    contentPadding = PaddingValues(1.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
                                ) {
                                    // Inner content including an icon and a text label
                                    Icon(
                                        painter = if(isLeftMenuVisible) painterResource(id = R.drawable.icon_left_arrow) else painterResource(id = R.drawable.icon_right_arrow),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier
                            //right menu
                            .align(Alignment.CenterEnd)
                        )
                        {
                            var isRightMenuVisible by remember { mutableStateOf(false) }
                            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                Button(
                                    onClick = { isRightMenuVisible = !isRightMenuVisible },
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .width(24.dp)
                                        .background(
                                            Color.Black.copy(0.3f),
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                bottomStart = 16.dp
                                            )
                                        ),
                                    contentPadding = PaddingValues(1.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
                                ) {
                                    // Inner content including an icon and a text label
                                    Icon(
                                        painter = if (isRightMenuVisible) painterResource(id = R.drawable.icon_right_arrow) else painterResource(id = R.drawable.icon_left_arrow),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                            }
                            // Right menu
                            AnimatedVisibility(
                                visible = isRightMenuVisible,
                                enter = slideInHorizontally(initialOffsetX = {it}),
                                exit = slideOutHorizontally(targetOffsetX = {it})
                            ) {
                                RightMenu(customSurfaceView)
                            }
                        }

                        Column(modifier = Modifier
                            //right menu
                            .align(Alignment.BottomCenter)
                        )
                        {
                            var isBottomMenuVisible by remember { mutableStateOf(false) }
                            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Button(
                                    onClick = { isBottomMenuVisible = !isBottomMenuVisible },
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .width(24.dp)
                                        .background(
                                            Color.Black.copy(0.3f),
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp
                                            )
                                        ),
                                    contentPadding = PaddingValues(1.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
                                ) {
                                    // Inner content including an icon and a text label
                                    Icon(
                                        painter = if (isBottomMenuVisible) painterResource(id = R.drawable.icon_down_arrow) else painterResource(id = R.drawable.icon_up_arrow),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                            }
                            // Bottom menu
                            AnimatedVisibility(
                                visible = isBottomMenuVisible,
                                enter = slideInVertically(initialOffsetY = {it}),
                                exit = slideOutVertically(targetOffsetY = {it})
                            ) {
                                BottomMenu(customSurfaceView)
                            }
                        }

                    }
                }
            }
        }
    }


    @Composable
    fun ColorSelectionBottomSheet(
        backgroundPalette: MutableList<Int>,
        drawingPalette: MutableList<Int>,
        onBackgroundColorSelected: (Int) -> Unit,
        onDrawingColorSelected: (Int) -> Unit,
//        onBackgroundSelected: (Boolean) -> Unit,
        onRandomColorSelected: (Boolean) -> Unit
    ) {
        var selectedBackgroundColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
        var selectedDrawingColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
//        var isBackgroundSelected by remember { mutableStateOf(false) }
        var isRandomColorEnabled by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(text = "Select Drawing Color", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(drawingPalette) { color ->
                    ColorChip(
                        color = Color(color),
                        isSelected = color == selectedDrawingColor.toInt(),
                        onColorSelected = {
                            selectedDrawingColor = color.toInt()
                            onDrawingColorSelected(selectedDrawingColor)
                            isRandomColorEnabled = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Select Background Color", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(backgroundPalette) { color ->
                    ColorChip(
                        color = Color(color),
                        isSelected = color == selectedBackgroundColor.toInt(),
                        onColorSelected = {
                            selectedBackgroundColor = color.toInt()
                            onBackgroundColorSelected(selectedBackgroundColor)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch for enabling random drawing color
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Random Drawing Color", modifier = Modifier.weight(1f))
                Switch(
                    checked = isRandomColorEnabled,
                    onCheckedChange = { isRandomColorEnabled = it; onRandomColorSelected(isRandomColorEnabled) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    fun ColorChip(
        color: Color,
        isSelected: Boolean,
        onColorSelected: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onColorSelected)
                .background(color = color)
                .border(
                    width = 2.dp,
                    color = if (isSelected) Color.Black else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }


    @Composable
    fun RoundedButton(modifier: Modifier,
                      onClick: () -> Unit,
                      icon: Painter,
                      description:String="") {
        Box(modifier = modifier.padding(horizontal = 10.dp)) {
            Button(
                onClick = onClick,
                shape = CircleShape,
                modifier = modifier
                    .size(42.dp)
                    .background(Color.Transparent)
                    .border(2.dp, Color.White, CircleShape),
                contentPadding = PaddingValues(1.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
            ) {
                // Inner content including an icon and a text label
                Icon(
                    painter = icon,
                    contentDescription = description,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    @Composable
    fun RightMenu(customSurfaceView: CustomSurfaceView) {
        Log.i(TAG, "right menu created ${customSurfaceView.getDropsSize()}")
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                RoundedButton (
                    onClick = {
                        customSurfaceView.toggleDrawingMode()
                    },
                    icon = if(customSurfaceView.isDrawModeActive()) painterResource(id = R.drawable.icon_drop) else painterResource(id = R.drawable.icon_lipstick),
                    modifier = Modifier
                )
                RoundedButton (
                    onClick = {
                        customSurfaceView.clear()
                    },
                    icon = painterResource(id = R.drawable.icon_clear),
                    modifier =Modifier
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
    }

    @Composable
    fun LeftMenu(customSurfaceView:CustomSurfaceView) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                RoundedButton(
                    onClick = {
                        customSurfaceView.setComb(PEN_TYPE.COMB_HORIZONTAL)
                    },
                    icon = painterResource(id = R.drawable.icon_horizontal_comb),
                    modifier =Modifier
                )
                RoundedButton (
                    onClick = {
                        customSurfaceView.setComb(PEN_TYPE.COMB_VERTICAL)
                    },
                    icon = painterResource(id = R.drawable.icon_vertical_comb),
                    modifier =Modifier
                )
                RoundedButton (
                    onClick = {
                        customSurfaceView.setComb(PEN_TYPE.PEN)
                    },
                    icon = painterResource(id = R.drawable.icon_pen),
                    modifier =Modifier
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

    }

    @Composable
    fun BottomMenu(customSurfaceView:CustomSurfaceView) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
        ) {

            ColorSelectionBottomSheet(
                backgroundPalette = backgroundPalette,
                drawingPalette = drawingPalette,
                onBackgroundColorSelected = { selectedColor -> customSurfaceView.setBKColor(selectedColor) },
                onDrawingColorSelected = {selectedColor -> customSurfaceView.setDrawingColor(selectedColor)},
                onRandomColorSelected = { isRandomColorEnabled -> customSurfaceView.setRandomColor(isRandomColorEnabled) }
            )
        }
    }
}


