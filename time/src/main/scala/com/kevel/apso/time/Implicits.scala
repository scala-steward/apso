package com.kevel.apso.time

import org.joda.time.{DateTime, DateTimeZone, Interval, LocalDate, LocalDateTime, LocalTime, Period, ReadableInterval}

/** Object containing implicit classes and methods related to datetime libraries.
  */
object Implicits {

  /** Implicit class that provides new methods for `LocalDates`.
    * @param d1
    *   the `LocalDate` to which the new methods are provided.
    */
  implicit class ApsoTimeLocalDate(val d1: LocalDate) extends AnyVal {

    /** Returns a `DateTime` corresponding to this `LocalDate` at UTC midnight.
      * @return
      *   a `DateTime` corresponding to this `LocalDate` at UTC midnight.
      */
    def utcDateTime: DateTime = d1.toDateTime(new LocalTime(0, 0), DateTimeZone.UTC)

    /** Returns a `DateTime` corresponding to this `LocalDate` at the latest valid time for the date.
      * @return
      *   a `DateTime` corresponding to this `LocalDate` at the latest valid time for the date.
      */
    def toDateTimeAtEndOfDay =
      d1.plusDays(1).toDateTimeAtStartOfDay.minusMillis(1)

    /** Returns a `DateTime` corresponding to this `LocalDate` at the latest valid time for the date on the given
      * `DateTimeZone`.
      * @param tz
      *   the target `DateTimeZone` for the returned `DateTime`
      * @return
      *   a `DateTime` corresponding to this `LocalDate` at the latest valid time for the date on the given
      *   `DateTimeZone`.
      */
    def toDateTimeAtEndOfDay(tz: DateTimeZone) =
      d1.plusDays(1).toDateTimeAtStartOfDay(tz).minusMillis(1)

    /** Returns an iterable interval starting at this `LocalDate` (inclusive) and ending at the given `LocalDate`
      * (inclusive), with a 1 day step.
      * @param d2
      *   the ending `LocalDate`
      * @return
      *   an iterable interval starting at this `LocalDate` (inclusive) and ending at the given `LocalDate` (inclusive),
      *   with a 1 day step.
      */
    def to(d2: LocalDate) =
      LocalDateInterval(
        IterableInterval(
          d1.toDateTimeAtStartOfDay,
          d2.toDateTimeAtStartOfDay.plusMillis(1),
          Period.days(1)
        )
      )

    /** Returns an iterable interval starting at this `LocalDate` (inclusive) and ending at the given `LocalDate`
      * (exclusive), with a 1 day step.
      * @param d2
      *   the ending `LocalDate`
      * @return
      *   an iterable interval starting at this `LocalDate` (inclusive) and ending at the given `LocalDate` (exclusive),
      *   with a 1 day step.
      */
    def until(d2: LocalDate) =
      LocalDateInterval(
        IterableInterval(
          d1.toDateTimeAtStartOfDay,
          d2.toDateTimeAtStartOfDay,
          Period.days(1)
        )
      )
  }

  /** Implicit class that provides new methods for `DateTimes`.
    * @param d1
    *   the `DateTime` to which the new methods are provided.
    */
  final implicit class ApsoTimeDateTime(val d1: DateTime) extends AnyVal {

    /** Returns a `LocalDateTime` corresponding to this `DateTime` at UTC.
      * @return
      *   a `LocalDateTime` corresponding to this `DateTime` at UTC.
      */
    def utcLocalDateTime: LocalDateTime = d1.withZone(DateTimeZone.UTC).toLocalDateTime

    /** Returns a `LocalDate` corresponding to this `DateTime` at UTC.
      * @return
      *   a `LocalDate` corresponding to this `DateTime` at UTC.
      */
    def utcLocalDate: LocalDate = d1.withZone(DateTimeZone.UTC).toLocalDate

    /** Returns `true` if the given `DateTime` is in the same day as this.
      * @param d2
      *   the second `DateTime`
      * @return
      *   `true` if the given `DateTime` is in the same day as this, `false` othwerwise.
      */
    def isSameDay(d2: DateTime) = d1.year == d2.year && d1.dayOfYear == d2.dayOfYear

    /** Retuns `true` if this `DateTime` is in the range between the two given `DateTimes`.
      * @param dStart
      *   the starting `DateTime`
      * @param dEnd
      *   the ending `DateTime`
      * @return
      *   `true` if this `DateTime` is in the range between the two given `DateTimes`, `false` otherwise.
      */
    def between(dStart: DateTime, dEnd: DateTime) = dStart.isBefore(d1) && d1.isBefore(dEnd)

    /** Returns an iterable interval starting at this `DateTime` (inclusive) and ending at the given `DateTime`
      * (inclusive), with a 1 day step.
      * @param d2
      *   the ending `DateTime`
      * @return
      *   an iterable interval starting at this `DateTime` (inclusive) and ending at the given `DateTime` (inclusive),
      *   with a 1 day step.
      */
    def to(d2: DateTime): IterableInterval =
      IterableInterval(d1, d2.plusMillis(1), Period.days(1))

    /** Returns an iterable interval starting at this `DateTime` (inclusive) and ending at the given `DateTime`
      * (exclusive), with a 1 day step.
      * @param d2
      *   the ending `DateTime`
      * @return
      *   an iterable interval starting at this `DateTime` (inclusive) and ending at the given `DateTime` (exclusive),
      *   with a 1 day step.
      */
    def until(d2: DateTime): IterableInterval = IterableInterval(d1, d2, Period.days(1))
  }

  /** Implicit class that provides new methods for `ReadableIntervals`.
    * @param interval
    *   the `ReadableInterval` to which the new methods are provided.
    */
  final implicit class ApsoTimeInterval(val interval: ReadableInterval) extends AnyVal {

    /** Partitions this time interval into a given number of equal subintervals.
      * @param n
      *   the number of subintervals to return
      * @return
      *   a sequence of time intervals resultant of the division of this interval in `n` equal parts.
      */
    def split(n: Int): Seq[ReadableInterval] = {
      require(n >= 0, "n must not be negative")
      if (n == 0) Seq.empty
      else {
        val q = (interval.toDuration.getMillis / n).toInt
        (0 until n).map { i =>
          new Interval(interval.getStart.plus(q * i), (interval.getStart.plus(q * (i + 1))))
        }
      }
    }
  }

  /** Implicit method that allows whe view of a time interval as an indexed sequence. Time intervals are split with a 1
    * day step.
    * @param interval
    *   the time interval to be iterated over
    * @return
    *   an iterable time interval.
    */
  implicit def intervalToStepped(interval: ReadableInterval): IterableInterval =
    IterableInterval(interval, Period.days(1))
}
