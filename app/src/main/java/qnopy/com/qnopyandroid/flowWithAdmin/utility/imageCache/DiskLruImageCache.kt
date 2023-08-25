package qnopy.com.qnopyandroid.flowWithAdmin.utility.imageCache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.jakewharton.disklrucache.DiskLruCache
import qnopy.com.qnopyandroid.BuildConfig
import qnopy.com.qnopyandroid.flowWithAdmin.utility.imageCache.DiskLRUUtils.getExternalCacheDir
import qnopy.com.qnopyandroid.flowWithAdmin.utility.imageCache.DiskLRUUtils.isExternalStorageRemovable
import java.io.*

class DiskLruImageCache(
    context: Context, uniqueName: String, diskCacheSize: Int,
    compressFormat: CompressFormat, quality: Int
) {
    private var mDiskCache: DiskLruCache? = null
    private var mCompressFormat = CompressFormat.JPEG
    private var mCompressQuality = 70

    @Throws(IOException::class)
    private fun writeBitmapToFile(
        bitmap: Bitmap,
        editor: DiskLruCache.Editor
    ): Boolean {
        var out: OutputStream? = null
        return try {
            out =
                BufferedOutputStream(editor.newOutputStream(0), DiskLRUUtils.IO_BUFFER_SIZE)
            bitmap.compress(mCompressFormat, mCompressQuality, out)
        } finally {
            out?.close()
        }
    }

    private fun getDiskCacheDir(
        context: Context,
        uniqueName: String
    ): File { // Check if media is mounted or storage is built-in, if so, try and use external cache dir
// otherwise use internal cache dir
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                !isExternalStorageRemovable
            ) getExternalCacheDir(context)!!.path else context.cacheDir.path
        return File(cachePath + File.separator + uniqueName)
    }

    fun put(key: String, data: Bitmap) {
        var editor: DiskLruCache.Editor? = null
        try {
            editor = mDiskCache!!.edit(key)
            if (editor == null) {
                return
            }
            if (writeBitmapToFile(data, editor)) {
                mDiskCache!!.flush()
                editor.commit()
                if (BuildConfig.DEBUG) {
                    Log.d("cache_qnopy_DISK_", "image put on disk cache $key")
                }
            } else {
                editor.abort()
                if (BuildConfig.DEBUG) {
                    Log.d(
                        "cache_qnopy_DISK_",
                        "ERROR on: image put on disk cache $key"
                    )
                }
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                Log.d("cache_qnopy_DISK_", "ERROR on: image put on disk cache $key")
            }
            try {
                editor?.abort()
            } catch (ignored: IOException) {
            }
        }
    }

    fun getBitmap(key: String): Bitmap? {
        var bitmap: Bitmap? = null
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            snapshot = mDiskCache!![key]
            if (snapshot == null) {
                return null
            }
            val `in` = snapshot.getInputStream(0)
            if (`in` != null) {
                val buffIn =
                    BufferedInputStream(`in`, DiskLRUUtils.IO_BUFFER_SIZE)
                bitmap = BitmapFactory.decodeStream(buffIn)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            snapshot?.close()
        }
        if (BuildConfig.DEBUG) {
            Log.d(
                "cache_qnopy_DISK_",
                if (bitmap == null) "" else "image read from disk $key"
            )
        }
        return bitmap
    }

    fun containsKey(key: String?): Boolean {
        var contained = false
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            snapshot = mDiskCache!![key]
            contained = snapshot != null
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            snapshot?.close()
        }
        return contained
    }

    fun clearCache() {
        if (BuildConfig.DEBUG) {
            Log.d("cache_qnopy_DISK_", "disk cache CLEARED")
        }
        try {
            mDiskCache!!.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val cacheFolder: File
        get() = mDiskCache!!.directory

    companion object {
        private const val APP_VERSION = BuildConfig.VERSION_CODE
        private const val VALUE_COUNT = 1
        private const val TAG = "DiskLruImageCache"
    }

    init {
        try {
            val diskCacheDir = getDiskCacheDir(context, uniqueName)
            mDiskCache = DiskLruCache.open(
                diskCacheDir,
                APP_VERSION,
                VALUE_COUNT,
                diskCacheSize.toLong()
            )
            mCompressFormat = compressFormat
            mCompressQuality = quality
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}