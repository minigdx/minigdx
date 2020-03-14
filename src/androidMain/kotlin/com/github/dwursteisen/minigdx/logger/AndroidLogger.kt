package com.github.dwursteisen.minigdx.logger

import android.util.Log

class AndroidLogger : Logger {

    override fun debug(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.DEBUG.ordinal) {
            Log.d(tag, message.invoke())
        }
    }

    override fun debug(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.DEBUG.ordinal) {
            Log.d(tag, message.invoke(), exception)
        }
    }

    override fun info(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.INFO.ordinal) {
            Log.i(tag, message.invoke())
        }
    }

    override fun info(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.INFO.ordinal) {
            Log.i(tag, message.invoke(), exception)
        }
    }

    override fun warn(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.WARN.ordinal) {
            Log.w(tag, message.invoke())
        }
    }

    override fun warn(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.WARN.ordinal) {
            Log.w(tag, message.invoke(), exception)
        }
    }

    override fun error(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.ERROR.ordinal) {
            Log.e(tag, message.invoke())
        }
    }

    override fun error(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.ERROR.ordinal) {
            Log.e(tag, message.invoke(), exception)
        }
    }

    override var rootLevel: Logger.LogLevel = Logger.LogLevel.INFO
}
