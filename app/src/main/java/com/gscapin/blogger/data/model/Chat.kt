package com.gscapin.blogger.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Chat(
    val text: List<Message>? = null,
    val user: List<User>? = null,
)
