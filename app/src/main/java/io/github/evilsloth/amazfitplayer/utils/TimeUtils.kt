package io.github.evilsloth.amazfitplayer.utils

import java.util.concurrent.TimeUnit

object TimeUtils {

    fun millisToTime(millis: Int): String {
        val millisLong = millis.toLong()

        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millisLong),
            TimeUnit.MILLISECONDS.toSeconds(millisLong) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisLong))
        )
    }

}