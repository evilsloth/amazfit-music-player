package io.github.evilsloth.amazfitplayer.settings

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import io.github.evilsloth.amazfitplayer.R
import io.github.evilsloth.amazfitplayer.mediaplayer.AmazfitMediaPlayer
import io.github.evilsloth.amazfitplayer.plugin.PluginPage
import io.github.evilsloth.amazfitplayer.queue.QueuePlaybackOrder

class SettingsPluginPage(
    private val mediaPlayer: AmazfitMediaPlayer,
    private val context: Context
) : PluginPage(R.layout.settings_page) {

    private lateinit var orderButton: ImageButton

    override fun onCreate(view: View) {
        orderButton = view.findViewById(R.id.playback_order_button)
        orderButton.setImageResource(mediaPlayer.playbackOrder.drawableResourceId)
        orderButton.setOnClickListener { nextPlaybackOrderSetting() }
    }

    private fun nextPlaybackOrderSetting() {
        val values = QueuePlaybackOrder.values()
        val currentIndex = values.indexOf(mediaPlayer.playbackOrder)
        val newOrder = if (currentIndex + 1 >= values.size) values[0] else values[currentIndex + 1]
        mediaPlayer.playbackOrder = newOrder
        orderButton.setImageResource(newOrder.drawableResourceId)
        Toast.makeText(context, newOrder.orderTipResourceId, Toast.LENGTH_SHORT).show()
    }

}