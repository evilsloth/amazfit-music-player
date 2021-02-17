package io.github.evilsloth.amazfitplayer

import android.os.Environment
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import io.github.evilsloth.amazfitplayer.mediaplayer.AmazfitMediaPlayer
import io.github.evilsloth.amazfitplayer.mediaplayer.PlaybackTimer
import io.github.evilsloth.amazfitplayer.mediaplayer.VOLUME_STEPS
import io.github.evilsloth.amazfitplayer.mediaplayer.track.FileTrack
import io.github.evilsloth.amazfitplayer.mediaplayer.track.Track
import io.github.evilsloth.amazfitplayer.mediaplayer.track.TrackQueue
import io.github.evilsloth.amazfitplayer.plugin.BasePlayerPlugin
import java.io.File

private const val TAG = "PlayerPlugin"

class PlayerPlugin : BasePlayerPlugin() {

    private lateinit var mediaPlayer: AmazfitMediaPlayer
    private lateinit var playbackTimer: PlaybackTimer
    private lateinit var trackQueue: TrackQueue

    private lateinit var playbackTimeText: TextView
    private lateinit var trackNameText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var decreaseVolumeButton: ImageButton
    private lateinit var increaseVolumeButton: ImageButton
    private lateinit var volumeProgressBar: ProgressBar

    override fun onViewCreated() {
        mediaPlayer = AmazfitMediaPlayer(context)
        playbackTimer = PlaybackTimer(this, host, mediaPlayer)
        trackQueue = TrackQueue()

        playbackTimeText = mainView.findViewById(R.id.playback_time_text)
        trackNameText = mainView.findViewById(R.id.track_name_text)
        playButton = mainView.findViewById(R.id.play_button)
        nextButton = mainView.findViewById(R.id.next_button)
        previousButton = mainView.findViewById(R.id.prevoius_button)
        decreaseVolumeButton = mainView.findViewById(R.id.decrease_volume_button)
        increaseVolumeButton = mainView.findViewById(R.id.increase_volume_button)
        volumeProgressBar = mainView.findViewById(R.id.volume_progress_bar)

        trackNameText.isSelected = true // activates marquee effect
        playButton.setOnClickListener { togglePlay() }
        nextButton.setOnClickListener { playNext() }
        previousButton.setOnClickListener { playPrevious() }
        decreaseVolumeButton.setOnClickListener { decreaseVolume() }
        increaseVolumeButton.setOnClickListener { increaseVolume() }

        val tracks = readFiles().map { FileTrack(it) }
        trackQueue.replace(tracks)
        trackQueue.current?.let { loadTrack(it) }
        mediaPlayer.addOnCompletedListener { playNext() }
        updatePlaybackControls()
        updateVolumeControls()
    }

    override fun onShow() {
        playbackTimer.start(playbackTimeText)
    }

    override fun onHide() {
        playbackTimer.stop()
    }

    private fun loadTrack(track: Track) {
        mediaPlayer.loadTrack(track)
        trackNameText.text = track.name
    }

    private fun togglePlay() {
        if (mediaPlayer.playing) {
            pausePlayback()
        } else {
            resumePlayback()
        }
    }

    private fun resumePlayback() {
        mediaPlayer.play()
        playButton.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun pausePlayback() {
        mediaPlayer.pause()
        playButton.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun playNext() {
        val next = trackQueue.next()
        if (next != null) {
            loadTrack(next)
            resumePlayback()
        } else {
            // resets last song so it starts from the beginning when play is pressed
            trackQueue.current?.let { loadTrack(it) }
            pausePlayback()
        }

        updatePlaybackControls()
    }

    private fun playPrevious() {
        trackQueue.previous()?.let {
            loadTrack(it)
            resumePlayback()
        }
        updatePlaybackControls()
    }

    private fun increaseVolume() {
        mediaPlayer.increaseVolume()
        updateVolumeControls()
    }

    private fun decreaseVolume() {
        mediaPlayer.decreaseVolume()
        updateVolumeControls()
    }

    private fun updatePlaybackControls() {
        playButton.isEnabled = !trackQueue.isEmpty
        nextButton.isEnabled = trackQueue.hasNext
        previousButton.isEnabled = trackQueue.hasPrevious
    }

    private fun updateVolumeControls() {
        volumeProgressBar.max = VOLUME_STEPS
        volumeProgressBar.progress = mediaPlayer.volume
        decreaseVolumeButton.isEnabled = mediaPlayer.canDecreaseVolume
        increaseVolumeButton.isEnabled = mediaPlayer.canIncreaseVolume
    }

    private fun readFiles(): List<File> {
        val path: String = Environment.getExternalStorageDirectory().toString() + "/Music"
        val directory = File(path)
        return directory.walk().filter { it.extension == "mp3" }.sorted().toList()
    }

}