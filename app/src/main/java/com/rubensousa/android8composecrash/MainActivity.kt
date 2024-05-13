package com.rubensousa.android8composecrash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.rubensousa.android8composecrash.ui.theme.Android8ComposeCrashTheme

class MainActivity : ComponentActivity() {

    private val reproduce = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (reproduce) {
            setContentView(R.layout.activity_layout)
            val composeView = findViewById<ComposeView>(R.id.composeView)
            composeView.setContent { Content() }
        } else {
            setContentView(R.layout.activity_layout_fix)
            val composeView = findViewById<Android8FixComposeView>(R.id.composeView)
            composeView.setContent { Content() }
        }
    }

    @Composable
    fun Content() {
        Android8ComposeCrashTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val focusRequester = remember { FocusRequester() }
                    var backgroundState by remember { mutableStateOf(Color.Gray) }
                    val context = LocalContext.current
                    val view = remember { SurfaceView(context) }
                    view.setBackgroundColor(backgroundState.toArgb())

                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                backgroundState = if (focusState.isFocused) {
                                    Color.Green
                                } else {
                                    Color.Gray
                                }
                            }
                            .focusable(),
                        factory = { view },
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        onClick = {
                            focusRequester.requestFocus()
                        }
                    ) {
                        Text(text = "Send focus to ComposeView")
                    }
                }
            }
        }
    }
}