package com.virtuix.lyricstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState


class MainActivity : ComponentActivity() {
	private val viewModel by viewModels<MainViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			val uiState = viewModel.uiState.collectAsState().value
			val errState = viewModel.errState.collectAsState().value

			MainScreen.Screen(
				viewModel,
				uiState,
				errState
			)
		}
	}
}

