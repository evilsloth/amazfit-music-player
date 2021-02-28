package io.github.evilsloth.amazfitplayer.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import io.github.evilsloth.amazfitplayer.queue.TrackQueue
import io.github.evilsloth.amazfitplayer.tracks.Track
import kotlin.math.ln

const val VOLUME_STEPS = 20

private const val TAG = "AmazfitMediaPlayer"

typealias OnTrackChangedListener = (track: Track?) -> Unit

class AmazfitMediaPlayer(
    private val context: Context,
    private val trackQueue: TrackQueue
) {

    val playing: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    val currentTrack: Track?
        get() = trackQueue.current

    val trackLength: Int
        get() = mediaPlayer?.duration ?: 0

    val trackPosition: Int
        get() = mediaPlayer?.currentPosition ?: 0

    var volume = VOLUME_STEPS / 2
        private set

    val canIncreaseVolume
        get() = volume < VOLUME_STEPS - 1

    val canDecreaseVolume
        get() = volume > 0

    val hasNext
        get() = trackQueue.hasNext

    val hasPrevious
        get() = trackQueue.hasPrevious

    val hasAnyTracks
        get() = !trackQueue.isEmpty

    private var mediaPlayer: MediaPlayer? = null

    private val onTrackChangedListeners = mutableSetOf<OnTrackChangedListener>()

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun next() {
        val next = trackQueue.next()
        if (next != null) {
            loadTrack(next)
            play()
        } else {
            trackQueue.current?.let { loadTrack(it) } // resets last song so it starts from the beginning when play is pressed
        }
        notifyTrackChanged()
    }

    fun previous() {
        trackQueue.previous()?.let {
            loadTrack(it)
            play()
        }
        notifyTrackChanged()
    }

    fun jumpToTrack(trackPosition: Int) {
        trackQueue.jumpToTrack(trackPosition)?.let {
            loadTrack(it)
            play()
        }
        notifyTrackChanged()
    }

    fun increaseVolume() {
        volume = if (volume + 1 < VOLUME_STEPS - 1) volume + 1 else VOLUME_STEPS - 1
        updateVolume()
    }

    fun decreaseVolume() {
        volume = if (volume - 1 > 0) volume - 1 else 0
        updateVolume()
    }

    fun replaceQueue(tracks: List<Track>) {
        trackQueue.replace(tracks)
        trackQueue.current?.let { loadTrack(it) }
        notifyTrackChanged()
    }

    fun addToQueue(tracks: List<Track>) {
        trackQueue.add(tracks)
    }

    fun addOnTrackChangedListener(listener: OnTrackChangedListener) {
        onTrackChangedListeners.add(listener)
    }

    private fun loadTrack(track: Track) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, track.uri)
        mediaPlayer?.setOnCompletionListener { next() }
        updateVolume()
    }

    private fun updateVolume() {
        val volumeScalar = (1 - (ln(VOLUME_STEPS.toFloat() - volume) / ln(VOLUME_STEPS.toFloat())))
        Log.d(TAG, "update volume $volume -> $volumeScalar")
        mediaPlayer?.setVolume(volumeScalar, volumeScalar);
    }

    private fun notifyTrackChanged() {
        onTrackChangedListeners.forEach { it(trackQueue.current) }
    }

}