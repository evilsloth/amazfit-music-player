package io.github.evilsloth.amazfitplayer.plugin

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

private const val CHANGE_PAGE_UP_THRESHOLD_PX = 10
private const val CHANGE_PAGE_DOWN_THRESHOLD_PX = 280

private const val MIN_MOVE_Y_DISTANCE_PX = 120
private const val MAX_MOVE_X_DISTANCE_PX = 50
private const val MOVE_TIME_LIMIT_MS = 500

class PluginPageChangingGestureListener(private val viewPager: ViewPager2) : GestureDetector.SimpleOnGestureListener() {

    private var lastTouchedScreenPart = ScreenPart.OTHER
    private var lastTouchDownTime = 0L
    private var pageSwitchHandled = false

    private var touchX = 0.0f
    private var touchY = 0.0f

    override fun onDown(e: MotionEvent?): Boolean {
        if (e == null) {
            return true
        }

        touchX = e.x
        touchY = e.y

        lastTouchDownTime = System.currentTimeMillis()
        pageSwitchHandled = false

        lastTouchedScreenPart = when {
            touchY < CHANGE_PAGE_UP_THRESHOLD_PX -> ScreenPart.TOP
            touchY > CHANGE_PAGE_DOWN_THRESHOLD_PX -> ScreenPart.BOTTOM
            else -> ScreenPart.OTHER
        }

        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        if (lastTouchedScreenPart == ScreenPart.OTHER || pageSwitchHandled || e2 == null || timeLimitReached()) {
            return true
        }

        val movedDistanceX = abs(touchX - e2.x)
        val movedDistanceY = abs(touchY - e2.y)

        if (movedDistanceX <= MAX_MOVE_X_DISTANCE_PX && movedDistanceY >= MIN_MOVE_Y_DISTANCE_PX) {
            when (lastTouchedScreenPart) {
                ScreenPart.TOP -> switchPageUp()
                ScreenPart.BOTTOM -> switchPageDown()
            }

            pageSwitchHandled = true
            return false
        }

        return true
    }

    private fun timeLimitReached(): Boolean {
        return System.currentTimeMillis() - lastTouchDownTime > MOVE_TIME_LIMIT_MS
    }

    private fun switchPageUp() {
        val currentItem = viewPager.currentItem

        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1, true)
        }
    }

    private fun switchPageDown() {
        val itemCount = viewPager.adapter?.itemCount ?: 0
        val currentItem = viewPager.currentItem

        if (currentItem < itemCount - 1) {
            viewPager.setCurrentItem(currentItem + 1, true)
        }
    }

    private enum class ScreenPart {
        TOP, BOTTOM, OTHER
    }

}