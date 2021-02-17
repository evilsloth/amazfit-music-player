package io.github.evilsloth.amazfitplayer.plugin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import clc.sliteplugin.flowboard.AbstractPlugin
import clc.sliteplugin.flowboard.ISpringBoardHostStub
import io.github.evilsloth.amazfitplayer.R

private const val TAG = "BasePlayerPlugin"

abstract class BasePlayerPlugin : AbstractPlugin() {

    lateinit var context: Context
    lateinit var mainView: View
    lateinit var host: ISpringBoardHostStub
    var active = false

    abstract fun onViewCreated()

    abstract fun onShow()

    abstract fun onHide()

    override fun getView(context: Context): View? {
        this.context = context
        mainView = LayoutInflater.from(context).inflate(R.layout.player_main, null)
        onViewCreated()
        Log.d(TAG, "getView")
        return mainView
    }

    override fun getWidgetIcon(paramContext: Context?): Bitmap? {
        return (context.resources.getDrawable(R.mipmap.ic_launcher) as BitmapDrawable).bitmap
    }

    override fun getWidgetTitle(paramContext: Context?): String? {
        return context.resources.getString(R.string.app_name_short)
    }

    override fun onBindHost(springBoardHostStub: ISpringBoardHostStub?) {
        Log.d(TAG, "onBindHost")
        host = springBoardHostStub!!
    }

    override fun onActive(paramBundle: Bundle?) {
        super.onActive(paramBundle)
        Log.d(TAG, "onActive")

        if (!active) {
            active = true
            onShow()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        if (!active) {
            active = true
            onShow()
        }
    }

    override fun onInactive(paramBundle: Bundle?) {
        super.onInactive(paramBundle)
        Log.d(TAG, "onInactive")

        if (active) {
            active = false
            onHide()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

        if (active) {
            active = false
            onHide()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        active = false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}