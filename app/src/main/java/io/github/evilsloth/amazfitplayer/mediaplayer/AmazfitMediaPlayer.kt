package io.github.evilsloth.amazfitplayer.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import io.github.evilsloth.amazfitplayer.R
import io.github.evilsloth.amazfitplayer.mediaplayer.volume.VolumeController
import io.github.evilsloth.amazfitplayer.queue.QueuePlaybackOrder
import io.github.evilsloth.amazfitplayer.queue.TrackQueue
import io.github.evilsloth.amazfitplayer.tracks.Track

private const val TAG = "AmazfitMediaPlayer"

typealias OnTrackChangedListener = (track: Track?) -> Unit

typealias OnControlStateChangedListener = () -> Unit

class AmazfitMediaPlayer(
    private val context: Context,
    private val trackQueue: TrackQueue,
    private val volumeController: VolumeController
) {

    val playing: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    val currentTrack: Track?
        get() = trackQueue.current

    val trackLength: Int
        get() = mediaPlayer?.duration ?: 0

    val trackPosition: Int
        get() = mediaPlayer?.currentPosition ?: 0

    val volume: Int
        get() = volumeController.volume

    val maxVolume: Int
        get() = volumeController.maxVolume

    val canIncreaseVolume
        get() = volumeController.canIncreaseVolume()

    val canDecreaseVolume
        get() = volumeController.canDecreaseVolume()

    val hasNext
        get() = trackQueue.hasNext

    val hasPrevious
        get() = trackQueue.hasPrevious

    val hasAnyTracks
        get() = !trackQueue.isEmpty

    var playbackOrder: QueuePlaybackOrder
        get() = trackQueue.playbackOrder
        set(value) {
            trackQueue.playbackOrder = value
            notifyControlStateChanged()
        }

    private var mediaPlayer: MediaPlayer? = null

    private val onTrackChangedListeners = mutableSetOf<OnTrackChangedListener>()

    private val onControlStateChangedListeners = mutableSetOf<OnControlStateChangedListener>()

    fun play() {
        mediaPlayer?.start()
        notifyControlStateChanged()
    }

    fun pause() {
        mediaPlayer?.pause()
        notifyControlStateChanged()
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
        volumeController.increaseVolume()
        notifyControlStateChanged()
    }

    fun decreaseVolume() {
        volumeController.decreaseVolume()
        notifyControlStateChanged()
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

    fun addOnControlStateChangedListeners(listener: OnControlStateChangedListener) {
        onControlStateChangedListeners.add(listener)
    }

    private fun loadTrack(track: Track) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, track.uri)
        mediaPlayer?.setOnCompletionListener { next() }
        mediaPlayer?.setVolume(1.0f, 1.0f)

        if (mediaPlayer == null) {
            Log.d(TAG, "Error creating media player for track " + track.name)
            Toast.makeText(context, context.getString(R.string.error_loading_track, track.name), Toast.LENGTH_LONG).show()
            next()
        }
    }

    private fun notifyTrackChanged() {
        onTrackChangedListeners.forEach { it(trackQueue.current) }
    }

    private fun notifyControlStateChanged() {
        onControlStateChangedListeners.forEach { it() }
    }

}