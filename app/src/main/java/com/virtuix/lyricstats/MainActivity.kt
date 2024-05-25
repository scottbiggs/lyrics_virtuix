package com.virtuix.lyricstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel = MainViewModel()

		setContent {
			val uiState = viewModel.uiState.collectAsState().value
			val errState = viewModel.errState.collectAsState().value

			MainScreen.Screen(
				viewModel = viewModel,
				uiState = uiState,
				errState = errState
			)
		}
	}
}

