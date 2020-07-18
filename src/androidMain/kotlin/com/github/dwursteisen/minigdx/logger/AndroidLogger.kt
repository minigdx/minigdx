package com.github.dwursteisen.minigdx.logger

import android.util.Log

class AndroidLogger(private val gameName: String) : Logger {

    override fun debug(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.DEBUG.ordinal) {
            Log.d("$gameName-$tag", message.invoke())
        }
    }

    override fun debug(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.DEBUG.ordinal) {
            Log.d("$gameName-$tag", message.invoke(), exception)
        }
    }

    override fun info(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.INFO.ordinal) {
            Log.i("$gameName-$tag", message.invoke())
        }
    }

    override fun info(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.INFO.ordinal) {
            Log.i("$gameName-$tag", message.invoke(), exception)
        }
    }

    override fun warn(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.WARN.ordinal) {
            Log.w("$gameName-$tag", message.invoke())
        }
    }

    override fun warn(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.WARN.ordinal) {
            Log.w("$gameName-$tag", message.invoke(), exception)
        }
    }

    override fun error(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.ERROR.ordinal) {
            Log.e("$gameName-$tag", message.invoke())
        }
    }

    override fun error(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.ERROR.ordinal) {
            Log.e("$gameName-$tag", message.invoke(), exception)
        }
    }

    override var rootLevel: Logger.LogLevel = Logger.LogLevel.INFO
}
