package com.mercadovivo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.navigation.MercadoVivoNavGraph
import com.mercadovivo.app.ui.theme.MercadoVivoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MercadoVivoTheme {
                MercadoVivoApp()
            }
        }
    }
}

@Composable
fun MercadoVivoApp() {
    val authViewModel = remember { AuthViewModel() }
    MercadoVivoNavGraph(authViewModel = authViewModel)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MercadoVivoTheme {
        MercadoVivoApp()
    }
}
