package com.shadowsmind.utils

import java.sql.{ Timestamp, Date ⇒ SqlDate }
import java.util.{ Calendar, Date }

object DateHelper {

  val millsInDay: Long = 86400000

  def now: Date =
    new Date()

  def time(time: Long): Timestamp =
    new Timestamp(time)

  def nowTime: Long =
    now.getTime

  def nowTimestamp: Timestamp =
    new Timestamp(nowTime)

  def yearsAgo(years: Int): Timestamp = {
    val calendar = Calendar.getInstance()
    calendar.roll(Calendar.YEAR, years)

    new Timestamp(calendar.getTime.getTime)
  }

  def currentSeconds: Long =
    System.currentTimeMillis() / 1000

  def backwardTimestamp(past: Long): Timestamp =
    new Timestamp(nowTime - past)

  def parse(source: String): SqlDate =
    SqlDate.valueOf(source)

  def passed(date: Date, timeLimit: Long): Boolean =
    nowTime > (date.getTime + timeLimit)

}
