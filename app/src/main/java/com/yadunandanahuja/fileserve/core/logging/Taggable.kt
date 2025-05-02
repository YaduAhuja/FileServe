package com.yadunandanahuja.fileserve.core.logging

interface Taggable {
    val TAG : String get() = this::class.java.simpleName
}