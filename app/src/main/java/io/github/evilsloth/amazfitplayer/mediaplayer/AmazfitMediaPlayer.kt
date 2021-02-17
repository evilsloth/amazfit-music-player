package io.github.evilsloth.amazfitplayer.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import io.github.evilsloth.amazfitplayer.mediaplayer.track.Track
import kotlin.math.ln

const val VOLUME_STEPS = 20

private const val TAG = "BasePlayerPlugin"

class AmazfitMediaPlayer(private val context: Context) {

    val playing: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    val trackLength: Int
        get() = mediaPlayer?.duration ?: 0

    val trackPosition: Int
        get() = mediaPlayer?.currentPosition ?: 0

    var volume = VOLUME_STEPS / 2
        private set

    val canIncreaseVolume
        get() = volume < VOLUME_STEPS - 1

    val canDecreaseVolume
        get() =  volume > 0

    private var mediaPlayer: MediaPlayer? = null

    private val onCompletedListeners = mutableSetOf<OnCompletedListener>()

    fun loadTrack(track: Track) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, track.uri)
        mediaPlayer?.setOnCompletionListener { onCompletedListeners.forEach { it() } }
        updateVolume()
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun increaseVolume() {
        volume = if (volume + 1 < VOLUME_STEPS - 1) volume + 1 else VOLUME_STEPS - 1
        updateVolume()
    }

    fun decreaseVolume() {
        volume = if (volume - 1 > 0) volume - 1 else 0
        updateVolume()
    }

    fun addOnCompletedListener(listener: OnCompletedListener) {
        onCompletedListeners.add(listener)
    }

    private fun updateVolume() {
        val volumeScalar = (1 - (ln(VOLUME_STEPS.toFloat() - volume) / ln(VOLUME_STEPS.toFloat())))
        Log.d(TAG, "update volume $volume -> $volumeScalar")
        mediaPlayer?.setVolume(volumeScalar, volumeScalar);
    }

}