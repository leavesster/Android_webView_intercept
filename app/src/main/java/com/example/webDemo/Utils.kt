package com.example.webDemo

fun cachedPath(url: String): String {
    return url.replace(Regex("https?://"), "")
}