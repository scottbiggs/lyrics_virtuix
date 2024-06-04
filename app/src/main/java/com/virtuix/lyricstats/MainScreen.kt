package com.virtuix.lyricstats

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.virtuix.lyricstats.ui.WordBox
import com.virtuix.lyricstats.ui.theme.LyricStatsTheme

@OptIn(ExperimentalMaterial3Api::class)
object MainScreen {

	@Composable
	fun Screen(
		viewModel: MainViewModelInterface,
		uiState: MainUiState,
		errState: ErrState
	) {

		val keyboardController = LocalSoftwareKeyboardController.current

		//-------------------
		//	compose state stuff
		//-------------------

		LyricStatsTheme {

			// display any necessary toasts
			displayErr(viewModel, errState)

			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
			) {

				ArtistTextField(viewModel, uiState)

				TitleTextField(viewModel, uiState)
/*
				//
				// switch between display longest word or most common word
				//
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center
				) {

					//
					// text for the left side of switch (false): Longest Word
					//
					Text(
						stringResource(R.string.longest_word),
						color = MaterialTheme.colorScheme.onSecondary,
						modifier =
							if (uiState.processChoice) {
								Modifier
									.align(Alignment.CenterVertically)
									.padding(4.dp)
							}
							else {
								Modifier
									.align(Alignment.CenterVertically)
									.background(
										MaterialTheme.colorScheme.secondary,
										shape = RoundedCornerShape(3.dp)
									)
									.border(
										2.dp,
										MaterialTheme.colorScheme.secondary,
										shape = RoundedCornerShape(3.dp)
									)
									.padding(4.dp)
							}
					)

					//
					// the switch itself
					// checked = most used, unchecked = longest word (default)
					//
					Switch(
						checked = uiState.processChoice,
						modifier = Modifier
							.padding(start = 8.dp, end = 8.dp),
						onCheckedChange = {
							viewModel.updateProcessChoice(it)
						}
					)

					//
					// text for right side of switch (true): Most Used Word
					//
					Text(
						stringResource(R.string.most_used_word),
						modifier =
							if (uiState.processChoice) {
								Modifier
									.align(Alignment.CenterVertically)
									.background(
										MaterialTheme.colorScheme.primary,
										shape = RoundedCornerShape(3.dp)
									)
									.border(
										2.dp,
										MaterialTheme.colorScheme.primary,
										shape = RoundedCornerShape(3.dp)
									)
									.padding(4.dp)
							}
							else {
								Modifier
									.align(Alignment.CenterVertically)
									.padding(4.dp)
							}
					)

				}
*/

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
		modifier = Modifier
			.fillMaxWidth()
			.padding(all = 8.dp)
	) {
		Text(text = stringResource(id = R.string.look_up_and_process_lyrics))
	}
}

@Composable
fun ThinkingSpinner() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		CircularProgressIndicator(
			modifier = Modifier.width(64.dp),
			color = MaterialTheme.colorScheme.tertiary,
		)
	}
}

@Composable
fun ShowWordAndDefinition(uiState : MainUiState) {
	val ctx = LocalContext.current

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
//	else {
//		Toast.makeText(ctx, "Hmmm, uiState.definition is black!", Toast.LENGTH_SHORT).show()
//	}
}

/**
 * Displays list of all the words in the selected song.
 * Each word is clickable.  Once clicked, a definition of
 * that word is displayed.
 */
@Composable
fun ShowWordList(viewModel: MainViewModelInterface, uiState: MainUiState) {

	val ctx = LocalContext.current
//	Log.d(TAG, "word -> ${uiState.currentWord}")
//	Log.d(TAG, "word list -> ${uiState.wordList}")

	if (uiState.wordList.isNotEmpty()) {

		Column {
//			Text(uiState.currentWord)
//			Text(uiState.wordList.toString())

			LazyVerticalGrid(
				columns = GridCells.Adaptive(minSize = 100.dp),
				verticalArrangement = Arrangement.spacedBy(3.dp),
				horizontalArrangement = Arrangement.spacedBy(3.dp),
				modifier = Modifier
					.padding(8.dp)
					.fillMaxWidth()
					.height(150.dp)
			) {
				items(uiState.wordList.toList().sortedWith(String.CASE_INSENSITIVE_ORDER)) { word ->
					WordBox(
						word = word,
						onClick = {
							viewModel.getDefinition(word)
						}
					)
				}
			}
		}
	}
}


@Composable
fun displayErr(viewModel: MainViewModelInterface, errState: ErrState) {
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