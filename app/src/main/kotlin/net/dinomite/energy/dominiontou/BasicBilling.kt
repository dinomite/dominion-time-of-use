package net.dinomite.energy.dominiontou

import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BasicBilling : EnergyPricer {
    private val periods: List<PricePeriod> = File("billing.csv").readLines()
        // Strip header row
        .drop(1)
        // Split to (start, end, kwh, cost, $/kwh)
        .map { it.split(",") }
        .map { (start, end, _, _, dollarsPerKwh) ->
            LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE)
            PricePeriod(
                LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE),
                BigDecimal(dollarsPerKwh)
            )
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
}
