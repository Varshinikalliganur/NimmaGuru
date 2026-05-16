package com.nimmaguru.domain.model

enum class UserRole {
    STUDENT,
    GURU;

    companion object {
        fun fromString(value: String?): UserRole = when (value?.uppercase()) {
            "GURU" -> GURU
            else -> STUDENT
        }
    }
}
