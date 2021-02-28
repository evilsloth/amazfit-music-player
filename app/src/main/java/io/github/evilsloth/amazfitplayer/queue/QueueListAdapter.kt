package io.github.evilsloth.amazfitplayer.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.evilsloth.amazfitplayer.R
import io.github.evilsloth.amazfitplayer.mediaplayer.AmazfitMediaPlayer

class QueueListAdapter(
    private val trackQueue: TrackQueue,
    private val mediaPlayer: AmazfitMediaPlayer
) : RecyclerView.Adapter<QueueListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackNameTextView: TextView = view.findViewById(R.id.track_name_text)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.queue_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackQueue.tracks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = trackQueue.tracks[position]
        holder.trackNameTextView.text = track.name
        holder.itemView.setOnClickListener { mediaPlayer.jumpToTrack(position) }

        val colorId = if (position == trackQueue.currentIndex) R.color.active_track_text_color else R.color.white
        @Suppress("DEPRECATION") val color = holder.trackNameTextView.context.resources.getColor(colorId)
        holder.trackNameTextView.setTextColor(color)
    }

}