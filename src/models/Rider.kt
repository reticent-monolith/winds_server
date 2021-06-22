package com.reticentmonolith.models


data class Rider(
    var weight: Int,
    var frontSlider: Slider? = null,
    var middleSlider: Slider? = null,
    var rearSlider: Slider? = null,
    var addedWeight: Int = 0,
    var trolley: Int = 0,
    var speed: Int? = 0
)
