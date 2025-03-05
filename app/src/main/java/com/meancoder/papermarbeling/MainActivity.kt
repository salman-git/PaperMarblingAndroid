package com.meancoder.papermarbeling

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
            var isOptionsMenuVisible by remember { mutableStateOf(false) }
            var isToolsMenuVisible by remember { mutableStateOf(false) }
            var isColorsMenuVisible by remember { mutableStateOf(false) }

            PaperMarbelingTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Outlined.Build, contentDescription = "Tools") },
                                label = { Text("Tools") },
                                selected = isToolsMenuVisible, // Change selection logic as needed
                                onClick = {
                                    isToolsMenuVisible = !isToolsMenuVisible
                                    isOptionsMenuVisible = false
                                    isColorsMenuVisible = false
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Outlined.Settings, contentDescription = "Options") },
                                label = { Text("Options") },
                                selected = isOptionsMenuVisible,
                                onClick = {
                                    isOptionsMenuVisible = !isOptionsMenuVisible
                                    isToolsMenuVisible = false
                                    isColorsMenuVisible = false
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Colors") },
                                label = { Text("Colors") },
                                selected = isColorsMenuVisible, // Change selection logic as needed
                                onClick = {
                                    isColorsMenuVisible = !isColorsMenuVisible
                                    isOptionsMenuVisible = false
                                    isToolsMenuVisible = false
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val customSurfaceView by remember {
                            mutableStateOf(CustomSurfaceView(this))
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red)
                            )
                            AndroidView(factory = { context ->
                                customSurfaceView
                            })

                            Column(
                                modifier = Modifier
                                    //right menu
                                    .align(Alignment.BottomCenter)
                            )
                            {
                                // Bottom menu
                                AnimatedVisibility(
                                    visible = isColorsMenuVisible,
                                    enter = slideInVertically(initialOffsetY = { it }),
                                    exit = slideOutVertically(targetOffsetY = { it })
                                ) {
                                    ColorsMenu(customSurfaceView)
                                }
                                var combWidth by remember { mutableFloatStateOf(100f) }
                                var radius by remember { mutableFloatStateOf(100f) }
                                var speed by remember { mutableFloatStateOf(10f) }
                                AnimatedVisibility(
                                    visible = isToolsMenuVisible,
                                    enter = slideInVertically(initialOffsetY = { it }),
                                    exit = slideOutVertically(targetOffsetY = { it })
                                ) {
                                    ToolsMenu(customSurfaceView, combWidth, radius, speed,
                                        onCombWidthChange = {
                                            combWidth=it
                                            customSurfaceView.setCombWidth(it)
                                                            },
                                        onRadiusChange = {
                                            radius=it
                                            customSurfaceView.setRadius(it)
                                                         },
                                        onSpeedChange={
                                            speed=it
                                            customSurfaceView.setSpeed(it)
                                        })
                                }
                                AnimatedVisibility(
                                    visible = isOptionsMenuVisible,
                                    enter = slideInVertically(initialOffsetY = { it }),
                                    exit = slideOutVertically(targetOffsetY = { it })
                                ) {
                                    OptionsMenu(customSurfaceView)
                                }
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

            Text(text = "Select Drawing Color", fontWeight = FontWeight.Bold, color=Color.White)
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

            Text(text = "Select Background Color", fontWeight = FontWeight.Bold, color=Color.White)
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
                Text(text = "Random Drawing Color", modifier = Modifier.weight(1f), color=Color.White)
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
    fun OptionsMenu(customSurfaceView: CustomSurfaceView) {
        val isDrawModeActive = remember { mutableStateOf(customSurfaceView.isDrawModeActive()) }
        Log.i(TAG, "right menu created ${customSurfaceView.getDropsSize()}")
            val toolButtons = arrayOf(
                ToolButton (
                    onClick = {
                        customSurfaceView.toggleDrawingMode()
                        isDrawModeActive.value = customSurfaceView.isDrawModeActive()
                    },
                    icon = if(isDrawModeActive.value)
                        painterResource(id = R.drawable.icon_drop)
                    else
                        painterResource(id = R.drawable.icon_lipstick),
                ),
                ToolButton (
                    onClick = {
                        customSurfaceView.clear()
                    },
                    icon = painterResource(id = R.drawable.icon_clear),
                ),
                ToolButton (
                    onClick = {
                        customSurfaceView.undo()
                        customSurfaceView.undo()
                    },
                    icon = painterResource(id = R.drawable.icon_undo),
                ),
                ToolButton (
                    onClick = {
                        customSurfaceView.redo()
                        customSurfaceView.redo()
                    },
                    icon = painterResource(id = R.drawable.icon_redo),
                ))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
        ) {

            LazyRow(
                modifier = Modifier
                    .padding(8.dp, 16.dp)
            ) {
                items(toolButtons) {button ->
                    RoundedButton(modifier = Modifier, onClick = button.onClick, icon = button.icon)
                }
            }
        }
    }
    data class ToolButton(
        val onClick: () -> Unit,
        val icon:Painter
    )

    @Composable
    fun ToolsMenu(customSurfaceView:CustomSurfaceView,
                  combWidth:Float,
                  radius:Float,
                  speed:Float,
                  onCombWidthChange:(Float) -> Unit,
                  onSpeedChange:(Float) -> Unit,
                  onRadiusChange:(Float) -> Unit ) {

        val toolButtons = arrayOf(
            ToolButton(
            onClick = {
                customSurfaceView.setComb(PEN_TYPE.COMB_HORIZONTAL)
            },
            icon = painterResource(id = R.drawable.icon_horizontal_comb),
        ),
                ToolButton(
                onClick = {
                    customSurfaceView.setComb(PEN_TYPE.COMB_VERTICAL)
                },
            icon = painterResource(id = R.drawable.icon_vertical_comb),
        ),
        ToolButton(
            onClick = {
                customSurfaceView.setComb(PEN_TYPE.PEN)
            },
            icon = painterResource(id = R.drawable.icon_pen),
        ),
        ToolButton(
            onClick = {
                customSurfaceView.setComb(PEN_TYPE.CIRCULAR_CLOCKWISE)
            },
            icon = painterResource(id = R.drawable.icon_clockwise),
        ),
        ToolButton(
            onClick = {
                customSurfaceView.setComb(PEN_TYPE.CIRCULAR_ANTICLOCKWISE)
            },
            icon = painterResource(id = R.drawable.icon_anticlockwise),
        ),
        ToolButton(
            onClick = {
                customSurfaceView.setComb(PEN_TYPE.SPIRAL_ANTICLOCKWISE)
            },
            icon = painterResource(id = R.drawable.icon_spiral_anticlock),
        ),
        ToolButton(
            onClick = {
                customSurfaceView.setComb(PEN_TYPE.SPIRAL_CLOCKWISE)
            },
            icon = painterResource(id = R.drawable.icon_spiral_clockwise),
        ))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
            ) {
            var currentTineTool by remember { mutableStateOf(customSurfaceView.getCurrentTineTool()) }

            Column(modifier=Modifier.padding(8.dp)) {
                if(currentTineTool == PEN_TYPE.COMB_HORIZONTAL || currentTineTool == PEN_TYPE.COMB_VERTICAL)
                    Column(){
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Comb Width", color=Color.White)
                            Text(text = kotlin.math.floor(combWidth).toInt().toString(), color=Color.White)
                        }
                        Slider(
                            value = combWidth,
                            onValueChange = onCombWidthChange,
                            valueRange = 50f..400f,
                            steps = 6
                        )
                    }
                if(currentTineTool == PEN_TYPE.CIRCULAR_CLOCKWISE || currentTineTool == PEN_TYPE.CIRCULAR_ANTICLOCKWISE)
                    Column {
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Radius", color=Color.White)
                            Text(text = kotlin.math.floor(radius).toInt().toString(), color=Color.White)
                        }
                        Slider(
                            value = radius,
                            onValueChange = onRadiusChange,
                            valueRange = 50f..400f,
                            steps = 6
                        )
                    }
                if(currentTineTool == PEN_TYPE.SPIRAL_ANTICLOCKWISE || currentTineTool == PEN_TYPE.SPIRAL_CLOCKWISE)
                    Column {
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Speed", color=Color.White)
                            Text(text = kotlin.math.floor(speed).toInt().toString(), color=Color.White)
                        }
                        Slider(
                            value = speed,
                            onValueChange = onSpeedChange,
                            valueRange = 10f..100f,
                            steps = 8
                        )
                        Text(text = "Spiral Radius", color=Color.White)
                    }
            }

            LazyRow(
                modifier = Modifier
                    .padding(8.dp, 16.dp)
            ) {
                items(toolButtons) {button ->
                    RoundedButton(modifier = Modifier, onClick = {
                        button.onClick()
                        currentTineTool = customSurfaceView.getCurrentTineTool()
                                                                 }, icon = button.icon)
                }
            }
        }
    }

    @Composable
    fun ColorsMenu(customSurfaceView:CustomSurfaceView) {
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


