package io.github.evilsloth.amazfitplayer.queue

import android.view.View
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import io.github.evilsloth.amazfitplayer.R
import io.github.evilsloth.amazfitplayer.mediaplayer.AmazfitMediaPlayer
import io.github.evilsloth.amazfitplayer.plugin.PluginPage
import io.github.evilsloth.amazfitplayer.wear.PatchedCurvingLayoutCallback

class QueuePluginPage(
    private val trackQueue: TrackQueue,
    private val mediaPlayer: AmazfitMediaPlayer
): PluginPage(R.layout.queue_page) {

    private lateinit var list: WearableRecyclerView

    override fun onCreate(view: View) {
        list = view.findViewById(R.id.queue_list)
        list.isEdgeItemsCenteringEnabled = true
        list.layoutManager = WearableLinearLayoutManager(view.context, PatchedCurvingLayoutCallback(view.context))
        list.adapter = QueueListAdapter(trackQueue, mediaPlayer)
        list.scrollToPosition(trackQueue.currentIndex)

        trackQueue.addOnQueueChangedListener { list.adapter?.notifyDataSetChanged() }
    }

    override fun onShowPage() {
        if (this::list.isInitialized) {
            list.scrollToPosition(trackQueue.currentIndex)
        }
    }
}