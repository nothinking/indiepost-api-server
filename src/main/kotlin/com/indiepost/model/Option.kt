package com.indiepost.model

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by jake on 7/25/16.
 */
@Entity
@Table(name = "Options")
data class Option(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        @NotNull
        @Size(min = 2, max = 20)
        var name: String? = null,

        @NotNull
        @Size(min = 2, max = 20)
        var value: String? = null
)
