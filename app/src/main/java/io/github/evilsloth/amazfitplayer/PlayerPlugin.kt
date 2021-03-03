package io.github.evilsloth.amazfitplayer

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.GestureDetector
import androidx.viewpager2.widget.ViewPager2
import io.github.evilsloth.amazfitplayer.mediaplayer.AmazfitMediaPlayer
import io.github.evilsloth.amazfitplayer.mediaplayer.MediaSessionController
import io.github.evilsloth.amazfitplayer.mediaplayer.PlaybackTimer
import io.github.evilsloth.amazfitplayer.mediaplayer.PlayerPluginPage
import io.github.evilsloth.amazfitplayer.mediaplayer.volume.VolumeController
import io.github.evilsloth.amazfitplayer.plugin.BasePlayerPlugin
import io.github.evilsloth.amazfitplayer.plugin.PluginPageChangingGestureListener
import io.github.evilsloth.amazfitplayer.plugin.PluginPagesAdapter
import io.github.evilsloth.amazfitplayer.queue.QueuePluginPage
import io.github.evilsloth.amazfitplayer.queue.TrackQueue
import io.github.evilsloth.amazfitplayer.settings.SettingsPluginPage
import io.github.evilsloth.amazfitplayer.tracks.FileTrack
import java.io.File


private const val TAG = "PlayerPlugin"

class PlayerPlugin : BasePlayerPlugin() {

    private lateinit var mediaPlayer: AmazfitMediaPlayer
    private lateinit var volumeController: VolumeController
    private lateinit var mediaSessionController: MediaSessionController
    private lateinit var playbackTimer: PlaybackTimer
    private lateinit var trackQueue: TrackQueue
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated() {
        viewPager = mainView.findViewById(R.id.main_pager)
        viewPager.isUserInputEnabled = false

        val gestureDetector = GestureDetector(context, PluginPageChangingGestureListener(viewPager))
        mainView.setOnTouchListener { view, event ->
            view.performClick()
            gestureDetector.onTouchEvent(event)
        }
    }

    override fun afterViewCreated(launcherContext: Context) {
        trackQueue = TrackQueue()
        volumeController = VolumeController(launcherContext)
        mediaPlayer = AmazfitMediaPlayer(launcherContext, trackQueue, volumeController)
        mediaSessionController = MediaSessionController(launcherContext, mediaPlayer)
        playbackTimer = PlaybackTimer(this, host)

        val tracks = readFiles().map { FileTrack(it) }
        mediaPlayer.replaceQueue(tracks)

        val pages = arrayOf(
            SettingsPluginPage(mediaPlayer, context),
            PlayerPluginPage(mediaPlayer, playbackTimer),
            QueuePluginPage(trackQueue, mediaPlayer)
        )

        viewPager.adapter = PluginPagesAdapter(pages)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pages[position].onShowPage()
            }
        })
        goToDefaultPage()
    }

    override fun onShow() {
        playbackTimer.start()
    }

    override fun onHide() {
        playbackTimer.stop()
    }

    override fun onInactive(paramBundle: Bundle?) {
        super.onInactive(paramBundle)
        goToDefaultPage()
    }

    private fun readFiles(): List<File> {
        val path: String = Environment.getExternalStorageDirectory().toString() + "/Music"
        val directory = File(path)
        return directory.walk().filter { it.extension == "mp3" }.sorted().toList()
    }

    private fun goToDefaultPage() {
        viewPager.setCurrentItem(1, false)
    }

}