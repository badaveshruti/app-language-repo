package qnopy.com.qnopyandroid.flowWithAdmin.utility

import android.util.Log

object Log {
    private var TAG = "Qnopy Logs"
    private var isDebug = true

    fun setTAG(tag: String) {
        TAG = tag
    }

    fun d(msg: String) {
        if (isDebug) Log.d(
            TAG,
            msg
        )
    }

    fun d(tag: String, msg: String) {
        if (isDebug) Log.d(tag, msg)
    }

    fun d(msg: Int) {
        if (isDebug) Log.d(
            TAG,
            msg.toString() + ""
        )
    }

    fun d(tag: String?, msg: Int) {
        if (isDebug) Log.d(tag, msg.toString() + "")
    }

    fun e(msg: String) {
        if (isDebug) Log.e(
            TAG,
            msg
        )
    }

    fun e(tag: String?, msg: String) {
        if (isDebug) Log.e(tag, msg)
    }

    fun e(msg: Int) {
        if (isDebug) Log.e(
            TAG,
            msg.toString() + ""
        )
    }

    fun e(tag: String?, msg: Int) {
        if (isDebug) Log.e(tag, msg.toString() + "")
    }

    fun i(msg: String) {
        if (isDebug) Log.i(
            TAG,
            msg
        )
    }

    fun i(tag: String, msg: String) {
        if (isDebug) Log.i(tag, msg)
    }

    fun i(msg: Int) {
        if (isDebug) Log.i(
            TAG,
            msg.toString() + ""
        )
    }

    fun w(tag: String, msg: Int) {
        if (isDebug) Log.w(tag, msg.toString() + "")
    }

    fun w(msg: String) {
        if (isDebug) Log.w(
            TAG,
            msg
        )
    }

    fun w(tag: String, msg: String) {
        if (isDebug) Log.w(tag, msg)
    }

    fun w(tag: String, msg: String, tr: java.lang.Exception) {
        if (isDebug) Log.w(tag, msg, tr)
    }

    fun w(msg: Int) {
        if (isDebug) Log.w(
            TAG,
            msg.toString() + ""
        )
    }
}