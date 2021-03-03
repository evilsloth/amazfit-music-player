package io.github.evilsloth.amazfitplayer.queue

import io.github.evilsloth.amazfitplayer.R

enum class QueuePlaybackOrder(val drawableResourceId: Int, val orderTipResourceId: Int) {

    NORMAL(R.drawable.ic_normal_order, R.string.order_normal),

    RANDOM(R.drawable.ic_random_order, R.string.order_random),

    REPEAT_ALL(R.drawable.ic_repeat, R.string.order_repeat_all)

}