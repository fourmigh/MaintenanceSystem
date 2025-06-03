package org.caojun.library.nettools

fun <T : Enum<T>> String.toEnum(enumClass: Class<T>): T? {
    return try {
        java.lang.Enum.valueOf(enumClass, this)
    } catch (e: IllegalArgumentException) {
        null
    }
}