package services

import java.time.{Clock, Instant, LocalDate}
import javax.inject.Inject

class DateTimeService @Inject() (clock: Clock) {

  def today: LocalDate = LocalDate.now(clock)

  def yesterday: LocalDate = today.minusDays(1)

  def now: Instant = Instant.now(clock)
}
