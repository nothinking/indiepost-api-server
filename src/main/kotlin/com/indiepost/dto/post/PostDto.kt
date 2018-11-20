package com.indiepost.dto.post

import com.indiepost.dto.ContributorDto
import com.indiepost.model.Profile
import java.io.Serializable
import java.time.Instant
import java.util.*

/**
 * Created by jake on 17. 1. 22.
 */
data class PostDto(
        var content: String? = null,

        var tags: List<String> = ArrayList(),

        var contributors: List<ContributorDto> = ArrayList(),

        var profiles: List<Profile> = ArrayList(),

        val lastRequested: Instant = Instant.now()
) : PostSummaryDto(), Serializable

