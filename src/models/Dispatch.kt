package com.reticentmonolith.models


import org.litote.kmongo.*
import java.time.LocalDateTime


data class Dispatch(
    var riders: MutableMap<Int, Rider?> = mutableMapOf(
        1 to null,
        2 to null,
        3 to null,
        4 to null
    ),
    var comment: String = "",
    var windDegrees: Int,
    var windSpeed: Double,
    var windsInstructor: String,
    var btRadio: String,
    var _id: Id<Dispatch> = newId(),
    var dateTime: String = LocalDateTime.now().toString(),
)

