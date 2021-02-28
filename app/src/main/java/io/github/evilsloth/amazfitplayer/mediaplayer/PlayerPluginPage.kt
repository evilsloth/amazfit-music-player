package io.github.evilsloth.amazfitplayer.mediaplayer

import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import io.github.evilsloth.amazfitplayer.R
import io.github.evilsloth.amazfitplayer.plugin.PluginPage
import io.github.evilsloth.amazfitplayer.tracks.Track
import io.github.evilsloth.amazfitplayer.utils.TimeUtils

class PlayerPluginPage(
    private val mediaPlayer: AmazfitMediaPlayer,
    private val playbackTimer: PlaybackTimer
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

        updateTrackInfo(mediaPlayer.currentTrack)
        updatePlaybackControls()
        updateVolumeControls()
    }

    private fun togglePlay() {
        if (mediaPlayer.playing) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.play()
        }
        updatePlaybackControls()
    }

    private fun playNext() {
        mediaPlayer.next()
    }

    private fun playPrevious() {
        mediaPlayer.previous()
    }

    private fun increaseVolume() {
        mediaPlayer.increaseVolume()
        updateVolumeControls()
    }

    private fun decreaseVolume() {
        mediaPlayer.decreaseVolume()
        updateVolumeControls()
    }

    private fun updateTrackInfo(track: Track?) {
        trackNameText.text = track?.name ?: "---"
        updatePlaybackControls()
    }

    private fun updatePlaybackControls() {
        playButton.setImageResource(if (mediaPlayer.playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        playButton.isEnabled = mediaPlayer.hasAnyTracks
        nextButton.isEnabled = mediaPlayer.hasNext
        previousButton.isEnabled = mediaPlayer.hasPrevious
        updatePlaybackTime()
    }

    private fun updateVolumeControls() {
        volumeProgressBar.max = VOLUME_STEPS
        volumeProgressBar.progress = mediaPlayer.volume
        decreaseVolumeButton.isEnabled = mediaPlayer.canDecreaseVolume
        increaseVolumeButton.isEnabled = mediaPlayer.canIncreaseVolume
    }

    private fun updatePlaybackTime() {
        val text = TimeUtils.millisToTime(mediaPlayer.trackPosition) + "/" + TimeUtils.millisToTime(mediaPlayer.trackLength)
        playbackTimeText.text = text
    }

}