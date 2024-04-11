package com.dingo.common.util

import java.util.*

// 下划线转驼峰
fun String.underlineToCamelCase(): String {
    val str = this.trim()
    if (str.isEmpty()) return ""
    val words = str.split("_")
    val camelCaseBuilder = StringBuilder(words[0])

    for (i in 1 until words.size) {
        camelCaseBuilder.append(words[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
    }

    return camelCaseBuilder.toString()
}