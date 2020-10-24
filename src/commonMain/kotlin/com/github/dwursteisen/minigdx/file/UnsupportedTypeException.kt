package com.github.dwursteisen.minigdx.file

import kotlin.reflect.KClass

class UnsupportedTypeException(val type: KClass<*>) : RuntimeException("Unsupported type '${type::class}'")
