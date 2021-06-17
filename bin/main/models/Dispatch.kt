package com.reticentmonolith.models


import org.litote.kmongo.*
import java.time.LocalDate
import java.time.LocalTime


data class Dispatch(
    var riders: MutableMap<Int, Rider?> = mutableMapOf(
        1 to null,
        2 to null,
        3 to null,
        4 to null
    ),
    var comment: String = "",
    var wind_degrees: Int,
    var wind_speed: Double,
    var winds_instructor: String,
    var bt_radio: String,
    var _id: Id<Dispatch> = newId(),
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now()
)

