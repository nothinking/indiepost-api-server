package com.indiepost.dto.post

data class PostFilter(
        val status: String,
        var q: String? = null,
        var isBroken: String? = null,
        var tag: String? = null
)