package io.github.evilsloth.amazfitplayer.plugin

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.viewpager2.widget.ViewPager2

private const val CHANGE_PAGE_UP_THRESHOLD_PX = 10
private const val CHANGE_PAGE_DOWN_THRESHOLD_PX = 280

class PluginPageChangingGestureListener(private val viewPager: ViewPager2) : GestureDetector.SimpleOnGestureListener() {

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        val itemCount = viewPager.adapter?.itemCount ?: 0
        val currentItem = viewPager.currentItem

        if (e1 != null && e1.y < CHANGE_PAGE_UP_THRESHOLD_PX) {
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true)
                return true
            }
        }

        if (e1 != null && e1.y > CHANGE_PAGE_DOWN_THRESHOLD_PX) {
            if (viewPager.currentItem < itemCount - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
                return true
            }
        }

        return false
    }

}