package com.pyloto.entregador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.pyloto.entregador.core.util.TokenManager
import com.pyloto.entregador.presentation.navigation.PylotoNavGraph
import com.pyloto.entregador.presentation.navigation.Routes
import com.pyloto.entregador.presentation.theme.PylotoTheme as PylotoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    private var isReady by mutableStateOf(false)
    private var startDestination by mutableStateOf(Routes.LOGIN)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            startDestination = resolveStartDestination()
            isReady = true
        }

        setContent {
            PylotoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isReady) {
                        val navController = rememberNavController()
                        PylotoNavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }

    private suspend fun resolveStartDestination(): String {
        if (!tokenManager.isLoggedIn()) {
            return Routes.LOGIN
        }
        return Routes.HOME
    }
}
