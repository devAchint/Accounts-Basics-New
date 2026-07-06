package com.techuntried.accountsbasics2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.ScienceQuizTheme
import com.techuntried.accountsbasics2.utils.subscribeToTopic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

//    @Inject
//    lateinit var consentManager: GoogleConsentManager

//    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
//        analytics = Firebase.analytics

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                BackgroundColor.toArgb(),
                BackgroundColor.toArgb(),
            ),
        )

//        consentManager.gatherConsent(activity = this){error->
//            if (error != null) {
//                Log.d("MYDEBUG", error.message ?: "")
//            }
//        }

        var uiState: MainUiState by mutableStateOf(value = MainUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.uiState.onEach {
                    uiState = it
                }.collect()
            }
        }
        splashScreen.setKeepOnScreenCondition { uiState.shouldKeepSplashScreen() }

        subscribeToTopic("All_General")
        setContent {
            ScienceQuizTheme {
                uiState.isFirstTime()?.let {
                    App(isFirstTime = it)
                }
            }
        }
    }
}

