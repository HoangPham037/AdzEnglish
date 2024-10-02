package com.adzenglish.adzenglish.models.local

data class Account(
    var id: Int = 0,
    var androidId: String = "",
    var name: String? ="",
    var gold: Int = 0,
    var imageView: String? ="",
    var point : Int = 0,
)
