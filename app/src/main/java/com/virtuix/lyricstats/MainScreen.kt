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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
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

		val keyboardController = LocalSoftwareKeyboardController.current

		//-------------------
		//	compose state stuff
		//-------------------

		LyricStatsTheme {

			// display any necessary toasts
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


			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
			) {

				//
				// artist textfield
				//
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

				//
				// title textfield
				//
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

				//
				// button to process the artist & title entries
				//
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

				//
				// current word and definition
				//	- only show when the conditions are just right
				//
				if (uiState.thinking == false) {
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


private const val TAG = "MainScreen"