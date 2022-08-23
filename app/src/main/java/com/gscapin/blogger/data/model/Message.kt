package com.gscapin.blogger.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Message(
    val text: String = "",
    @ServerTimestamp
    val date: Date? = null
)
