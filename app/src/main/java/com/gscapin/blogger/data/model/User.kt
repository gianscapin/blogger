package com.gscapin.blogger.data.model

data class User(
    val username: String = "",
    val email: String = "",
    var userPhotoUrl: String = "",
    var contacts: List<ContactMessage>? = null,
    var id: String? = null
){}
