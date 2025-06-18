package com.dh.ondot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform