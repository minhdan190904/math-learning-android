package com.trilogy.mathlearning.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(val id: String? = null, val email: String? = null, val name: String? = null)


@Serializable
data class UserResDto(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val point: Int? = null,
    val like: Int? = null,
    val lastLogin: Long? = null
)
