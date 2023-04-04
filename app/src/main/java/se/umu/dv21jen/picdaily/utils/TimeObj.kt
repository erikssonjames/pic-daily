package se.umu.dv21jen.picdaily.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("SimpleDateFormat")
class TimeObj(
    dateString: String?, // YYYY-mm-dd
    timeZone: String // GMT, CET, AST
) {
    private var zone: TimeZone
    private var date: LocalDate

    init {
        zone = TimeZone.getTimeZone(timeZone)

        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zone.toZoneId())

        val ld = LocalDate.parse(dateString, df)
        date = ld
    }

    fun isWithinToday(): Boolean {
        val today = LocalDate.now(zone.toZoneId())

        Log.d("TimeObj", "todayCalender (year: ${today.year}, month: ${today.month}, day: ${today.dayOfYear})")
        Log.d("TimeObj", "todayCalender (year: ${date.year}, month: ${date.month}, day: ${date.dayOfYear})")

        return today.year == date.year &&
                today.dayOfMonth == date.dayOfMonth
    }
}