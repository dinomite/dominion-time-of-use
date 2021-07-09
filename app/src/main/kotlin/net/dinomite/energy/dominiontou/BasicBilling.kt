package net.dinomite.energy.dominiontou

import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class BasicBilling : EnergyPricer {
    private val periods: List<PricePeriod> = File("billing.csv").readLines()
        // Strip header row
        .drop(1)
        // Split to (start, end, kwh, cost, $/kwh)
        .map { it.split(",") }
        .map { (start, end, _, _, dollarsPerKwh) ->
            PricePeriod(parseDate(start), parseDate(end), BigDecimal(dollarsPerKwh))
        }

    class PricePeriod(
        private val startDate: LocalDate,
        private val endDate: LocalDate,
        val pricePerKwh: BigDecimal
    ) {
        fun appliesTo(date: LocalDate): Boolean = startDate.isBefore(date.plusDays(1)) && endDate.isAfter(date)
    }

    override fun price(datetime: LocalDateTime): BigDecimal {
        return periods.first { it.appliesTo(datetime.toLocalDate()) }.pricePerKwh
    }

    companion object {
        private fun parseDate(date: String): LocalDate {
            return date.split("-").let { (year, month, day) -> LocalDate.of(year.toInt(), month.toInt(), day.toInt()) }
        }
    }
}
