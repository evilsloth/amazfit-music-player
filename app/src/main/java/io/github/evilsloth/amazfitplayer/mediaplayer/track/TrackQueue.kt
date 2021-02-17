package io.github.evilsloth.amazfitplayer.mediaplayer.track

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

    private val queue: MutableList<Track> = mutableListOf()

    private var currentIndex = 0

    fun next(): Track? {
        if (!hasNext) return null

        currentIndex++

        if (repeat && currentIndex >= queue.size) {
            currentIndex = 0
        }

        return queue[currentIndex]
    }

    fun previous(): Track? {
        if (!hasPrevious) return null

        currentIndex--

        if (repeat && currentIndex < 0) {
            currentIndex = queue.size - 1
        }

        return queue[currentIndex]
    }

    fun replace(tracks: List<Track>) {
        queue.clear()
        add(tracks)
    }

    fun add(tracks: List<Track>) {
        queue.addAll(tracks)
    }

}