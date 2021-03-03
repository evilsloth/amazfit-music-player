package io.github.evilsloth.amazfitplayer.mediaplayer.volume

import android.content.Context
import android.media.AudioManager

class VolumeController(context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    var volume: Int
        get() = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        set(value) = audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0)

    val maxVolume
        get() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    fun canIncreaseVolume() = volume < maxVolume

    fun canDecreaseVolume() = volume > 0

    fun increaseVolume() {
        // first increase from zero with adjust function does not raise volume to 1 (had to click twice)
        if (volume == 0) {
            volume = 1
        } else {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
        }
    }

    fun decreaseVolume() {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
    }

}