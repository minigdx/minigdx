package com.github.dwursteisen.minigdx.file

class EarlyAccessException(val filename: String, val property: String) :
    RuntimeException("Content of file '$filename' accessed before being loaded by the property '$property'!")
