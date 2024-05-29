package com.virtuix.lyricstats

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtuix.lyricstats.ui.theme.LyricStatsTheme

@OptIn(ExperimentalMaterial3Api::class)
object MainScreen {

	@Composable
	fun Screen(
		viewModel: IMainViewModel,
		uiState: MainUiState,
		errState: ErrState
	) {
		/** Variable to control the switch.  True -> Most Used, False -> Longest word */
		var wordProcessChoice by remember { mutableStateOf(false) }

		/** lets us know when the first process has occurred */
		var firstLookupOccurred by remember { mutableStateOf(false) }

		val keyboardController = LocalSoftwareKeyboardController.current

		LyricStatsTheme {

			if (errState.errState) {
				val ctx = LocalContext.current

				// error happened.
				if (errState.errDescId != 0) {
					Log.e(TAG, errState.getDescString(ctx))
				}
				if (errState.errMsgId != 0) {
					Toast.makeText(ctx, errState.getMsgString(ctx), Toast.LENGTH_LONG).show()
				}
				viewModel.processError()	// recompose without error
			}


			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
			) {
				TextField(
					value = uiState.artist,
					onValueChange = viewModel::updateArtist,
					placeholder = { Text(text = stringResource(id = R.string.artist)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
				)
				TextField(
					value = uiState.songTitle,
					onValueChange = viewModel::updateSongTitle,
					placeholder = { Text(text = stringResource(id = R.string.song_title)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
				)


				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center
				) {
					Text(
						stringResource(R.string.longest_word),
						color = MaterialTheme.colorScheme.onSecondary,
						modifier =
							if (wordProcessChoice) {
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

					// checked = most used, unchecked = longest word (default)
					Switch(
						checked = wordProcessChoice,
						modifier = Modifier
							.padding(start = 8.dp, end = 8.dp),
						onCheckedChange = {
							wordProcessChoice = it
							viewModel.updateProcessChoice(wordProcessChoice)
						}
					)

					Text(
						stringResource(R.string.most_used_word),
						modifier =
							if (wordProcessChoice) {
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

				Button(
					onClick = {
						keyboardController?.hide()
						viewModel::lookUpAndProcessLyrics.invoke()
						firstLookupOccurred = true
					},
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
				) {
					Text(text = stringResource(id = R.string.look_up_and_process_lyrics))
				}

				//
				// current word and definition
				//	- only show when the conditions are just right
				//
				if ((uiState.thinking == false) and
						firstLookupOccurred and
						uiState.artist.isNotBlank() and
						uiState.songTitle.isNotBlank()) {

					Text(
						uiState.definition,
						modifier = Modifier
							.fillMaxWidth()
							.padding(all = 8.dp)
					)
				}

			}

			// Show the spinner while thinking
			if (uiState.thinking) {
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
		}
	}
}


@Preview(showSystemUi = true)
@Composable
fun MyPreview() {
	val viewModelPreview = MainViewModelPreview()

	MainScreen.Screen(
		viewModelPreview,
		viewModelPreview.uiState,
		viewModelPreview.errState
	)
}


private const val TAG = "MainScreen"