package com.virtuix.lyricstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
	private val viewModel = MainViewModel()

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

