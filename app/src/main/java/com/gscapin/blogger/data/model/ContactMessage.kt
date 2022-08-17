package com.gscapin.blogger.data.model

data class ContactMessage(
    val users: List<User>,
    val idMessage: String? = null
)
