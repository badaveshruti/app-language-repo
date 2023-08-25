package qnopy.com.qnopyandroid.flowWithAdmin.utility

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.flowWithAdmin.utility.imageCache.DiskLruImageCache

object Utils {

    fun <T : Parcelable?> getParcelable(
        intent: Intent,
        key: String,
        className: Class<T>
    ): T {
        return if (SDK_INT >= 33)
            intent.getParcelableExtra(key, className)!!
        else
            intent.getParcelableExtra(key)!!
    }

    fun <T : java.io.Serializable?> getParcelable(
        bundle: Bundle,
        key: String,
        className: Class<T>
    ): T {
        return if (SDK_INT >= 33)
            bundle.getSerializable(key, className)!!
        else
            bundle.getSerializable(key) as T
    }

    fun <T : java.io.Serializable?> getSerializable(
        intent: Intent,
        key: String,
        className: Class<T>
    ): T {
        return if (SDK_INT >= 33)
            intent.getSerializableExtra(key, className)!!
        else
            intent.getSerializableExtra(key) as T
    }

    fun <T : java.io.Serializable?> getSerializable(
        bundle: Bundle,
        key: String,
        className: Class<T>
    ): T {
        return if (SDK_INT >= 33)
            bundle.getSerializable(key, className)!!
        else
            bundle.getSerializable(key) as T
    }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun setToolbarTitleAndBackBtn(
        title: String,
        isBackBtnEnable: Boolean,
        activity: AppCompatActivity
    ) {
        if (isBackBtnEnable) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            activity.supportActionBar?.setDisplayShowHomeEnabled(true)
            activity.supportActionBar?.setHomeButtonEnabled(true)
        }

        if (title.trim().isNotEmpty())
            activity.title = title
    }

    fun getDisplayMetrics(activity: AppCompatActivity): DisplayMetrics {
        val outMetrics = DisplayMetrics()

        if (SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = activity.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        return outMetrics
    }

    fun getDateTimeDaysAgo(millis: Long): String {
        return try {
            var ago =
                DateUtils.getRelativeTimeSpanString(
                    millis,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
            if (ago.contains("0 minutes ago"))
                ago = "moments ago"
            ago.toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun initDiskCache(context: Context) {
        val memClass =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        val cacheSize = 1024 * 1024 * memClass / 8
        ScreenReso.diskLruCache = DiskLruImageCache(
            context, GlobalStrings.LRU_CACHE_NAME,
            cacheSize, Bitmap.CompressFormat.JPEG, 80
        )
    }
}