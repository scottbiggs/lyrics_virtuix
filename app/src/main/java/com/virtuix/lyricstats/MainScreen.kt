package com.virtuix.lyricstats

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.virtuix.lyricstats.ui.WordBox
import com.virtuix.lyricstats.ui.theme.LyricStatsTheme

object MainScreen {

	@Composable
	fun Screen(
		viewModel: MainViewModelInterface,
		uiState: MainUiState,
		errState: ErrState
	) {

		val keyboardController = LocalSoftwareKeyboardController.current

		val configuration = LocalConfiguration.current


		LyricStatsTheme {

			// display any necessary toasts
			DisplayErr(viewModel, errState)

			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
				PortraitUI(viewModel, uiState, keyboardController)
			}
			else {
				LandscapeUI(viewModel, uiState, keyboardController, configuration.screenWidthDp)
			}


		}
	}
}

@Composable
fun LandscapeUI(
	viewModel: MainViewModelInterface,
	uiState: MainUiState,
	keyboardController: SoftwareKeyboardController?,
	screenSize: Int
) {
	Row(
		modifier = Modifier
			.fillMaxSize()
	) {
		Column(
			Modifier.width((screenSize * 0.4).toInt().dp)
		) {
			ArtistTextField(viewModel = viewModel, uiState = uiState)
			TitleTextField(viewModel = viewModel, uiState = uiState)
			ProcessButton(viewModel = viewModel, keyboardController = keyboardController)
			// todo: put filter switch here
		}

		Column(
			Modifier.verticalScroll(rememberScrollState())
		) {
			if (uiState.thinking) {
				ThinkingSpinner()
			}
			else {
				ShowWordList(viewModel, uiState)
				ShowWordAndDefinition(uiState)
			}
		}
	}
}



@Composable
fun PortraitUI(
	viewModel: MainViewModelInterface,
	uiState: MainUiState,
	keyboardController: SoftwareKeyboardController?
) {
	Column(
		Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
	) {
		ArtistTextField(viewModel, uiState)
		TitleTextField(viewModel, uiState)
		ProcessButton(viewModel, keyboardController)

		// todo: put filter switch here

		if (uiState.thinking) {
			ThinkingSpinner()
		}
		else {
			ShowWordList(viewModel, uiState)
			ShowWordAndDefinition(uiState)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistTextField(viewModel: MainViewModelInterface, uiState: MainUiState) {

	TextField(
		value = uiState.artist,
		onValueChange = {
			viewModel.updateArtist(it)
		},
		label = {
			Text(stringResource(R.string.artist))
		},
		placeholder = {
			Text(stringResource(R.string.artist))
		},
		singleLine = true,
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp)
	)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTextField(viewModel: MainViewModelInterface, uiState: MainUiState) {
	TextField(
		value = uiState.songTitle,
		onValueChange = {
			viewModel.updateSongTitle(it)
		},
		label = { Text(stringResource(R.string.song_title)) },
		placeholder = { Text(text = stringResource(id = R.string.song_title)) },
		modifier = Modifier
			.fillMaxWidth()
			.padding(all = 8.dp)
	)
}

@Composable
fun ProcessButton(viewModel: MainViewModelInterface, keyboardController: SoftwareKeyboardController?) {
	Button(
		onClick = {
			keyboardController?.hide()
			viewModel::lookUpAndProcessLyrics.invoke()
		},
		Modifier
			.fillMaxWidth()
			.padding(all = 8.dp)
	) {
		Text(text = stringResource(id = R.string.look_up_and_process_lyrics))
	}
}

@Composable
fun ThinkingSpinner() {
	Box(
		Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		CircularProgressIndicator(
			Modifier.width(64.dp).height(64.dp),
			color = MaterialTheme.colorScheme.tertiary,
		)
	}
}

@Composable
fun ShowWordAndDefinition(uiState : MainUiState) {

	if (uiState.definition.isNotBlank()) {
		Log.d(TAG, "uiState.definition = ${uiState.definition}")
		Text(
			uiState.definition,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			modifier = Modifier
				.fillMaxWidth()
				.padding(all = 8.dp)
				.background(MaterialTheme.colorScheme.primaryContainer)
				.padding(all = 12.dp)
		)
	}
}

/**
 * Displays list of all the words in the selected song.
 * Each word is clickable.  Once clicked, a definition of
 * that word is displayed.
 */
@Composable
fun ShowWordList(viewModel: MainViewModelInterface, uiState: MainUiState) {

	if (uiState.wordMap.isNotEmpty()) {

		Column {

			LazyVerticalGrid(
				columns = GridCells.Adaptive(minSize = 100.dp),
				verticalArrangement = Arrangement.spacedBy(3.dp),
				horizontalArrangement = Arrangement.spacedBy(3.dp),
				modifier = Modifier
					.padding(8.dp)
					.fillMaxWidth()
					.height(150.dp)
			) {
				items(uiState.wordMap.toList()) { pair ->
					WordBox(
						word = pair.first,
						count = pair.second,
						onClick = {
							viewModel.getDefinition(pair.first)
						}
					)
				}
			}
		}
	}
}


@Composable
fun DisplayErr(viewModel: MainViewModelInterface, errState: ErrState) {
	if (errState.errState) {
		val ctx = LocalContext.current

		// error happened.
		if (errState.errDescId != 0) {
			Log.e(TAG, errState.getDescString(ctx))
		}
		if (errState.errMsgId != 0) {
			Toast.makeText(ctx, errState.getMsgString(ctx), Toast.LENGTH_LONG).show()
		}
		viewModel.processError(true)	// recompose without error
	}
}

private const val TAG = "MainScreen"