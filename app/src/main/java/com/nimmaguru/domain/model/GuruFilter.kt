package com.nimmaguru.domain.model

data class GuruFilter(
    val query: String = "",
    val city: String? = null,
    val subject: String? = null,
    val minExperience: Int? = null,
)
