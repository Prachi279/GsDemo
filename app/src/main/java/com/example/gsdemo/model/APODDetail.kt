package com.example.gsdemo.model

/**
 * The APODDetail class,to hold APOD detail
 */
data class APODDetail (
    var date: String,
    var explanation: String,
    var hdurl:String,
    var media_type: String,
    var service_version: String,
    var title: String,
    var url: String,
    var isFav:Boolean=false
)