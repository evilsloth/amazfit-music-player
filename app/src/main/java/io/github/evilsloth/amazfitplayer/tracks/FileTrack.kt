package io.github.evilsloth.amazfitplayer.tracks

import android.net.Uri
import java.io.File

class FileTrack(private val file: File): Track {

    override val uri: Uri
        get() = Uri.fromFile(file)

    override val name: String
        get() = file.name

}