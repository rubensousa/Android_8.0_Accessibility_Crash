package com.rubensousa.android8composecrash

import android.os.Bundle
import android.view.View
import android.view.autofill.AutofillManager
import android.widget.EditText
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rubensousa.android8composecrash.ui.theme.Android8ComposeCrashTheme

class MainActivity : ComponentActivity() {

    private val reproduce = true
    private lateinit var autoFillManager: AutofillManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoFillManager = getSystemService(AutofillManager::class.java)

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
                        .padding(24.dp)
                ) {
                    val context = LocalContext.current
                    var showField by remember { mutableStateOf(true) }
                    val view = remember {
                        EditText(context).apply {
                            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
                            setAutofillHints(View.AUTOFILL_HINT_NAME)
                        }
                    }
                    if (showField) {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            update = {
                                autoFillManager.requestAutofill(view)
                            },
                            factory = { view },
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        onClick = {
                            showField = !showField
                        }
                    ) {
                        Text(text = "Show/hide TextField")
                    }
                }
            }
        }
    }
}