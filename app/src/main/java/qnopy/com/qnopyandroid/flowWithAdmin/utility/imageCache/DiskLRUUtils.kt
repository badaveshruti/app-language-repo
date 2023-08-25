package qnopy.com.qnopyandroid.flowWithAdmin.utility.imageCache

import android.content.Context
import android.os.Environment
import java.io.File

object DiskLRUUtils {
    const val IO_BUFFER_SIZE = 8 * 1024
    val isExternalStorageRemovable: Boolean
        get() = Environment.isExternalStorageRemovable()

    @JvmStatic
    fun getExternalCacheDir(context: Context): File? {
        return context.externalCacheDir
    }
}