package com.meancoder.papermarbeling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.meancoder.papermarbeling.ui.theme.PaperMarbelingTheme

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaperMarbelingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreenContent(
                        this,
                        onTimeout = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent(context: Context, onTimeout: () -> Unit) {
    val privacyPolicyUrl = "https://tfortechie.blogspot.com/2024/05/privacy-policy-paper-marbling.html" // Replace with your actual URL

    // Use a LaunchedEffect to delay the navigation to MainActivity
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onTimeout()
    }

    // UI layout
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Image in the center
            Image(
                painter = painterResource(id = R.drawable.playstore_icon), // Ensure the drawable name matches
                contentDescription = null,
                modifier = Modifier.size(128.dp) // Adjust the size as needed
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy policy link
            Text(
                text = "Privacy Policy",
                color = Color.Blue,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse(privacyPolicyUrl)
                    }
                    startActivity(context, intent, null)
                }
            )
        }
    }
}
