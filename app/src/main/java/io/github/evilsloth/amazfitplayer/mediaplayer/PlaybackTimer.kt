package io.github.evilsloth.amazfitplayer.mediaplayer

import android.util.Log
import android.widget.TextView
import clc.sliteplugin.flowboard.AbstractPlugin
import clc.sliteplugin.flowboard.ISpringBoardHostStub
import io.github.evilsloth.amazfitplayer.utils.TimeUtils
import java.util.*

private const val TAG = "BasePlayerPlugin"
private const val UPDATE_INTERVAL_MS = 1000L

class PlaybackTimer(
    private val plugin: AbstractPlugin,
    private val host: ISpringBoardHostStub,
    private val mediaPlayer: AmazfitMediaPlayer
) {

    private var timer: Timer? = null

    fun start(textView: TextView) {
        Log.d(TAG, "Playback timer updater started")
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                host.runTaskOnUI(plugin) {
                    val text = TimeUtils.millisToTime(mediaPlayer.trackPosition) + "/" + TimeUtils.millisToTime(mediaPlayer.trackLength)
                    textView.text = text
                }
            }
        }, 0, UPDATE_INTERVAL_MS)
    }

    fun stop() {
        Log.d(TAG, "Playback timer updater stopped")
        timer?.cancel()
        timer = null
    }

}