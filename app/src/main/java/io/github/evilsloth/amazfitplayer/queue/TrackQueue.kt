package io.github.evilsloth.amazfitplayer.queue

import io.github.evilsloth.amazfitplayer.tracks.Track

typealias OnQueueChangedListener = () -> Unit

class TrackQueue {

    val repeat = false

    val hasNext
        get() = if (repeat) isEmpty else currentIndex < queue.size - 1

    val hasPrevious
        get() = if (repeat) isEmpty else currentIndex > 0

    val isEmpty
        get() = queue.isEmpty()

    val current: Track?
        get() = if (currentIndex < queue.size) queue[currentIndex] else null

    val tracks: List<Track>
        get() = queue

    var currentIndex = 0

    private val onQueueChangedListeners = mutableSetOf<OnQueueChangedListener>()

    private val queue: MutableList<Track> = mutableListOf()

    fun next(): Track? {
        if (!hasNext) return null

        currentIndex++

        if (repeat && currentIndex >= queue.size) {
            currentIndex = 0
        }

        onQueueChangedListeners.forEach { it() }
        return queue[currentIndex]
    }

    fun previous(): Track? {
        if (!hasPrevious) return null

        currentIndex--

        if (repeat && currentIndex < 0) {
            currentIndex = queue.size - 1
        }

        onQueueChangedListeners.forEach { it() }
        return queue[currentIndex]
    }

    fun jumpToTrack(trackPosition: Int): Track? {
        if (trackPosition > queue.size - 1) return null

        currentIndex = trackPosition

        onQueueChangedListeners.forEach { it() }
        return queue[currentIndex]
    }

    fun replace(tracks: List<Track>) {
        queue.clear()
        add(tracks)
    }

    fun add(tracks: List<Track>) {
        queue.addAll(tracks)
        onQueueChangedListeners.forEach { it() }
    }

    fun addOnQueueChangedListener(listener: OnQueueChangedListener) {
        onQueueChangedListeners.add(listener)
    }

}