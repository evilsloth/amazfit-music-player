package io.github.evilsloth.amazfitplayer.mediaplayer

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import io.github.evilsloth.amazfitplayer.R
import io.github.evilsloth.amazfitplayer.mediaplayer.headphones.HeadphonesConnectionManager
import io.github.evilsloth.amazfitplayer.plugin.PluginPage
import io.github.evilsloth.amazfitplayer.tracks.Track
import io.github.evilsloth.amazfitplayer.utils.TimeUtils

private const val TAG = "PlayerPluginPage"

class PlayerPluginPage(
    private val mediaPlayer: AmazfitMediaPlayer,
    private val playbackTimer: PlaybackTimer,
    private val headphonesConnectionManager: HeadphonesConnectionManager,
    private val context: Context
) : PluginPage(R.layout.player_page) {

    private lateinit var playbackTimeText: TextView
    private lateinit var trackNameText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var decreaseVolumeButton: ImageButton
    private lateinit var increaseVolumeButton: ImageButton
    private lateinit var volumeProgressBar: ProgressBar

    override fun onCreate(view: View) {
        playbackTimeText = view.findViewById(R.id.playback_time_text)
        trackNameText = view.findViewById(R.id.track_name_text)
        playButton = view.findViewById(R.id.play_button)
        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.prevoius_button)
        decreaseVolumeButton = view.findViewById(R.id.decrease_volume_button)
        increaseVolumeButton = view.findViewById(R.id.increase_volume_button)
        volumeProgressBar = view.findViewById(R.id.volume_progress_bar)

        playButton.setOnClickListener { togglePlay() }
        nextButton.setOnClickListener { playNext() }
        previousButton.setOnClickListener { playPrevious() }
        decreaseVolumeButton.setOnClickListener { decreaseVolume() }
        increaseVolumeButton.setOnClickListener { increaseVolume() }

        trackNameText.isSelected = true // activates marquee effect
        playbackTimer.tickListener = this::updatePlaybackTime
        mediaPlayer.addOnTrackChangedListener { updateTrackInfo(it) }
        mediaPlayer.addOnControlStateChangedListeners { updateControlsState() }
        headphonesConnectionManager.addOnHeadphonesConnectedListener { updateControlsState() } // volume changes on headphones connect

        updateTrackInfo(mediaPlayer.currentTrack)
        updateControlsState()
    }

    private fun togglePlay() {
        if (!headphonesConnectionManager.isConnected) {
            Toast.makeText(context, R.string.headphones_not_connected, Toast.LENGTH_LONG).show()
        }

        if (mediaPlayer.playing) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.play()
        }
    }

    private fun playNext() {
        mediaPlayer.next()
    }

    private fun playPrevious() {
        mediaPlayer.previous()
    }

    private fun increaseVolume() {
        mediaPlayer.increaseVolume()
    }

    private fun decreaseVolume() {
        mediaPlayer.decreaseVolume()
    }

    private fun updateTrackInfo(track: Track?) {
        trackNameText.text = track?.name ?: "---"
    }

    private fun updateControlsState() {
        updatePlaybackControls()
        updateVolumeControls()
    }

    private fun updatePlaybackControls() {
        playButton.setImageResource(if (mediaPlayer.playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        playButton.isEnabled = mediaPlayer.hasAnyTracks
        nextButton.isEnabled = mediaPlayer.hasNext
        previousButton.isEnabled = mediaPlayer.hasPrevious
        updatePlaybackTime()
    }

    private fun updateVolumeControls() {
        volumeProgressBar.max = mediaPlayer.maxVolume
        volumeProgressBar.progress = mediaPlayer.volume
        decreaseVolumeButton.isEnabled = mediaPlayer.canDecreaseVolume
        increaseVolumeButton.isEnabled = mediaPlayer.canIncreaseVolume
        Log.d(TAG, "volume max = " + mediaPlayer.maxVolume + " current = " + mediaPlayer.volume)
    }

    private fun updatePlaybackTime() {
        val text = TimeUtils.millisToTime(mediaPlayer.trackPosition) + "/" + TimeUtils.millisToTime(mediaPlayer.trackLength)
        playbackTimeText.text = text
    }

}