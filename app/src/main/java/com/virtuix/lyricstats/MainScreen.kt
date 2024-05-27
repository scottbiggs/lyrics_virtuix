package com.virtuix.lyricstats

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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


			Column(modifier = Modifier.fillMaxSize()) {
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
				Button(
					onClick = viewModel::lookUpAndProcessLyrics,
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
				) {
					Text(text = stringResource(id = R.string.look_up_and_process_lyrics))
				}
				Text(
					text = stringResource(id = R.string.longest_lyric, uiState.longestWord),
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
				)
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


const val TAG = "MainScreen"