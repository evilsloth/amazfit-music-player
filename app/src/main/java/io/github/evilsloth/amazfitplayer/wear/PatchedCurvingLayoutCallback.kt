package io.github.evilsloth.amazfitplayer.wear

import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager.LayoutCallback
import io.github.evilsloth.amazfitplayer.R

/* Class from android wear library patched to work with api 21 */
class PatchedCurvingLayoutCallback(context: Context) : LayoutCallback() {
    private val mCurvePath: Path
    private val mPathMeasure: PathMeasure
    private var mCurvePathHeight = 0
    private var mXCurveOffset: Int
    private var mPathLength = 0f
    private var mCurveBottom = 0f
    private var mCurveTop = 0f
    private var mLineGradient = 0f
    private val mPathPoints = FloatArray(2)
    private val mPathTangent = FloatArray(2)
    private val mAnchorOffsetXY = FloatArray(2)
    private var mParentView: RecyclerView? = null
    private var mIsScreenRound: Boolean
    private var mLayoutWidth = 0
    private var mLayoutHeight = 0
    override fun onLayoutFinished(child: View, parent: RecyclerView) {
        if (mParentView !== parent || mParentView != null && (mParentView!!.width != parent.width
                    || mParentView!!.height != parent.height)
        ) {
            mParentView = parent
            mLayoutWidth = mParentView!!.width
            mLayoutHeight = mParentView!!.height
        }
        if (mIsScreenRound) {
            maybeSetUpCircularInitialLayout(mLayoutWidth, mLayoutHeight)
            mAnchorOffsetXY[0] = mXCurveOffset.toFloat()
            mAnchorOffsetXY[1] = child.height / 2.0f
            adjustAnchorOffsetXY(child, mAnchorOffsetXY)
            val minCenter = (-child.height).toFloat() / 2
            val maxCenter = mLayoutHeight + child.height.toFloat() / 2
            val range = maxCenter - minCenter
            val verticalAnchor = child.top.toFloat() + mAnchorOffsetXY[1]
            val mYScrollProgress = (verticalAnchor + Math.abs(minCenter)) / range
            mPathMeasure.getPosTan(mYScrollProgress * mPathLength, mPathPoints, mPathTangent)
            val topClusterRisk =
                (Math.abs(mPathPoints[1] - mCurveBottom) < EPSILON
                        && minCenter < mPathPoints[1])
            val bottomClusterRisk =
                (Math.abs(mPathPoints[1] - mCurveTop) < EPSILON
                        && maxCenter > mPathPoints[1])
            // Continue offsetting the child along the straight-line part of the curve, if it
            // has not gone off the screen when it reached the end of the original curve.
            if (topClusterRisk || bottomClusterRisk) {
                mPathPoints[1] = verticalAnchor
                mPathPoints[0] = Math.abs(verticalAnchor) * mLineGradient
            }

            // Offset the View to match the provided anchor point.
            val newLeft = (mPathPoints[0] - mAnchorOffsetXY[0]).toInt()
            child.offsetLeftAndRight(newLeft - child.left)
            val verticalTranslation = mPathPoints[1] - verticalAnchor
            child.translationY = verticalTranslation
        } else {
            child.translationY = 0f
        }
    }

    /**
     * Override this method if you wish to adjust the anchor coordinates for each child view
     * during a layout pass. In the override set the new desired anchor coordinates in
     * the provided array. The coordinates should be provided in relation to the child view.
     *
     * @param child          The child view to which the anchor coordinates will apply.
     * @param anchorOffsetXY The anchor coordinates for the provided child view, by default set
     * to a pre-defined constant on the horizontal axis and half of the
     * child height on the vertical axis (vertical center).
     */
    fun adjustAnchorOffsetXY(child: View?, anchorOffsetXY: FloatArray?) {
        return
    }

    @VisibleForTesting
    fun setRound(isScreenRound: Boolean) {
        mIsScreenRound = isScreenRound
    }

    @VisibleForTesting
    fun setOffset(offset: Int) {
        mXCurveOffset = offset
    }

    /** Set up the initial layout for round screens.  */
    private fun maybeSetUpCircularInitialLayout(width: Int, height: Int) {
        // The values in this function are custom to the curve we use.
        if (mCurvePathHeight != height) {
            mCurvePathHeight = height
            mCurveBottom = -0.048f * height
            mCurveTop = 1.048f * height
            mLineGradient = 0.5f / 0.048f
            mCurvePath.reset()
            mCurvePath.moveTo(0.5f * width, mCurveBottom)
            mCurvePath.lineTo(0.34f * width, 0.075f * height)
            mCurvePath.cubicTo(
                0.22f * width, 0.17f * height, 0.13f * width, 0.32f * height, 0.13f * width,
                height / 2.toFloat()
            )
            mCurvePath.cubicTo(
                0.13f * width,
                0.68f * height,
                0.22f * width,
                0.83f * height,
                0.34f * width,
                0.925f * height
            )
            mCurvePath.lineTo(width / 2.toFloat(), mCurveTop)
            mPathMeasure.setPath(mCurvePath, false)
            mPathLength = mPathMeasure.length
        }
    }

    companion object {
        private const val EPSILON = 0.001f
    }

    init {
        mCurvePath = Path()
        mPathMeasure = PathMeasure()
        mIsScreenRound = true
        mXCurveOffset = context.resources.getDimensionPixelSize(
            R.dimen.ws_wrv_curve_default_x_offset
        )
    }
}