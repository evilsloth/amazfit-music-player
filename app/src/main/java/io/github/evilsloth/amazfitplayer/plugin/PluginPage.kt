package io.github.evilsloth.amazfitplayer.plugin

import android.view.View

abstract class PluginPage(val layout: Int) {

    open fun onCreate(view: View) {

    }

    open fun onShowPage() {

    }

}