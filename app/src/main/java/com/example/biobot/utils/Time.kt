package com.example.biobot.utils

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class Time {

    companion object {
        fun timeStamp(): String {

            val timeStamp = Timestamp(System.currentTimeMillis())
            val sdf = SimpleDateFormat("HH:mm")
            val time = sdf.format(Date(timeStamp.time))

            return time.toString()
        }
    }
}