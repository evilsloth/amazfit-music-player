package io.github.evilsloth.amazfitplayer.mediaplayer

import android.util.Log
import clc.sliteplugin.flowboard.AbstractPlugin
import clc.sliteplugin.flowboard.ISpringBoardHostStub
import java.util.*

private const val TAG = "PlaybackTimer"
private const val UPDATE_INTERVAL_MS = 1000L

typealias OnTickListener = () -> Unit

class PlaybackTimer(
    private val plugin: AbstractPlugin,
    private val host: ISpringBoardHostStub
) {

    private var timer: Timer? = null

    var tickListener: OnTickListener? = null

    fun start() {
        Log.d(TAG, "Playback timer updater started")
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                host.runTaskOnUI(plugin) {
                    tickListener?.invoke()
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