package com.reticentmonolith.models


data class Rider(
    var weight: Int,
    var front_slider: Slider? = null,
    var middle_slider: Slider? = null,
    var rear_slider: Slider? = null,
    var added_weight: Int = 0,
    var trolley: Int = 0,
    var speed: Int? = 0
)
