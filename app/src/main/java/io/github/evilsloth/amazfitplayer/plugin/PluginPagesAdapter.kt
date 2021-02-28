package io.github.evilsloth.amazfitplayer.plugin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PluginPagesAdapter(private val pages: Array<PluginPage>) : RecyclerView.Adapter<PluginPagesAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return position;
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(pages[viewType].layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        pages[position].onCreate(viewHolder.itemView)
    }

    override fun getItemCount() = pages.size

}
