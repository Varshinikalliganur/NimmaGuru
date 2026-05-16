package com.nimmaguru.domain

import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.GuruFilter
import java.util.Locale

object GuruFilterMatcher {
    fun matches(user: AppUser, filter: GuruFilter): Boolean {
        val q = filter.query.trim().lowercase(Locale.getDefault())
        if (q.isNotEmpty()) {
            val haystack = listOfNotNull(
                user.displayName,
                user.city,
                user.bio,
                user.subjects.joinToString(" "),
            ).joinToString(" ").lowercase(Locale.getDefault())
            if (!haystack.contains(q)) return false
        }
        filter.city?.takeIf { it.isNotBlank() }?.let { c ->
            if (!user.city.orEmpty().equals(c, ignoreCase = true)) return false
        }
        filter.subject?.takeIf { it.isNotBlank() }?.let { s ->
            val has = user.subjects.any { it.equals(s, ignoreCase = true) }
            if (!has) return false
        }
        filter.minExperience?.let { min ->
            val years = user.yearsExperience ?: return false
            if (years < min) return false
        }
        return true
    }

    fun filterList(users: List<AppUser>, filter: GuruFilter): List<AppUser> =
        users.filter { matches(it, filter) }
}
