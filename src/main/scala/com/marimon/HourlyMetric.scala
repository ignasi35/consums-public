package com.marimon

import java.time.{LocalDateTime, ZoneId}


case class HourlyMetric(dateTime: LocalDateTime, consumWh: Int) {
  val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
  val ISO = dateTime.toString
}
