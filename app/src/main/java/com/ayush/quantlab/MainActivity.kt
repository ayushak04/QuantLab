package com.ayush.quantlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ayush.quantlab.navigation.QuantLabNavGraph
import com.ayush.quantlab.ui.theme.QuantLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuantLabTheme {
                QuantLabNavGraph()
            }
        }
    }
}
