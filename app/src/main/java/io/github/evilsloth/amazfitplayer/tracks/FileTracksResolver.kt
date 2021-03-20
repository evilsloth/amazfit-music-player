package io.github.evilsloth.amazfitplayer.tracks

import android.os.Environment
import java.io.File

private val SUPPORTED_EXTENSIONS = listOf("3gp", "mp3", "mp4", "m4a", "aac", "flac", "ogg", "mkv", "wav")

class FileTracksResolver {

    enum class PathType {
        FILE, DIRECTORY, DIRECTORY_DEEP
    }

    @Suppress("DEPRECATION")
    private val basePath: String = Environment.getExternalStorageDirectory().toString()

    fun resolve(path: String, pathType: PathType): List<FileTrack> {
        val file = File(basePath + File.separator + path)

        return when (pathType) {
            PathType.DIRECTORY -> resolveFiles(file.listFiles()!!.asSequence())
            PathType.DIRECTORY_DEEP -> resolveFiles(file.walk())
            PathType.FILE -> listOf(FileTrack(file))
        }
    }

    private fun resolveFiles(files: Sequence<File>): List<FileTrack> {
        return files
            .filter { SUPPORTED_EXTENSIONS.contains(it.extension) }
            .sorted()
            .map { FileTrack(it) }
            .toList()
    }

}