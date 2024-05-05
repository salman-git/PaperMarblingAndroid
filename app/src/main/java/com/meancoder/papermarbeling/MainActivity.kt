package com.meancoder.papermarbeling

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.meancoder.papermarbeling.ui.theme.PaperMarbelingTheme

class MainActivity : ComponentActivity() {
    private val TAG: String = "MainActivity"

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
                    var isLeftMenuVisible by remember { mutableStateOf(true) }
                    var isRightMenuVisible by remember { mutableStateOf(true) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(factory = { context ->
                            customSurfaceView
                        })
                        Button(
                            onClick = { isRightMenuVisible = !isRightMenuVisible},
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(text = "Hide Right Menu")
                        }

                        // Left menu Hide button
                        Button(
                            onClick = { isLeftMenuVisible = !isLeftMenuVisible },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(text = "Hide Left Menu")
                        }

                        AnimatedVisibility(
                            visible = isLeftMenuVisible,
                            enter = slideInHorizontally(),
                            exit = slideOutHorizontally(),
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                                LeftMenu(customSurfaceView)
                        }

                        // Right menu
                        AnimatedVisibility(
                            visible = isRightMenuVisible,
                            enter = slideInHorizontally(initialOffsetX = { it }),
                            exit = slideOutHorizontally(targetOffsetX = { it }),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                                RightMenu(customSurfaceView)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun RightMenu(customSurfaceView: CustomSurfaceView) {
        Log.i(TAG, "right menu created ${customSurfaceView.getDropsSize()}")
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        customSurfaceView.toggleDrawingMode()
                    },
                    modifier = Modifier.padding(bottom = 8.dp) // Add padding between buttons
                ) {
                    Text(text = "Toggle Mode")
                }
                Button(
                    onClick = {
                        customSurfaceView.clear()
                    }
                ) {
                    Text(text = "Clear")
                }
            }

    }

    @Composable
    fun LeftMenu(customSurfaceView:CustomSurfaceView) {

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = {
                        customSurfaceView.setComb(CustomSurfaceView.PEN_TYPE.COMB_HORIZONTAL)
                    }
                ) {
                    Text(text = "HC") //horizontal comb
                }
                Button(
                    onClick = {
                        customSurfaceView.setComb(CustomSurfaceView.PEN_TYPE.COMB_VERTICAL)
                    }
                ) {
                    Text(text = "YC") //vertical comb
                }
                Button(
                    onClick = {
                        customSurfaceView.setComb(CustomSurfaceView.PEN_TYPE.PEN)
                    }
                ) {
                    Text(text = "Pen")
                }
            }

    }
}


