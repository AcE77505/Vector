package org.matrix.vector.daemon.data

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FIX-1: make module-load failures observable.
 *
 * Every module the framework cannot load is dropped silently: [FileSystem.loadModule] returns
 * [ModuleLoad.Unusable], ConfigCache skips it, and nothing anywhere records why. A module that
 * never runs then leaves no evidence at all to work from.
 *
 * Appending to a plain file under the log directory makes the reason survive long enough to be
 * read back from the device.
 */
internal object ForkLog {
    private const val PATH = "/data/adb/lspd/log/fork-load.log"
    private val lock = Any()
    private val fmt = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)

    fun append(tag: String, message: String) {
        val line = fmt.format(Date()) + " [" + tag + "] " + message + "\n"
        synchronized(lock) {
            runCatching { File(PATH).appendText(line) }
            runCatching { android.util.Log.i("VectorFork", tag + ": " + message) }
        }
    }
}
