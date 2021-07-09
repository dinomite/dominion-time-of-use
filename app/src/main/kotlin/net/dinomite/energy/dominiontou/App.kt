package net.dinomite.energy.dominiontou

import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val bill = File("billing.csv").readLines()
    // Strip header row
    .drop(1)
    // Split to (start, end, kwh, cost, $/kwh)
    .map { it.split(",") }
    .map { (start, end, kwh, cost, dollarsPerKwh) ->
        BillingPeriod(
            LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE),
            LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE),
            kwh.toInt(),
            BigDecimal(cost.substring(1)),
            BigDecimal(dollarsPerKwh)
        )
    }

data class BillingPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val kwh: Int,
    val cost: BigDecimal,
    val pricePerKwh: BigDecimal
)

fun main() {
    val kwhEachHour = energyMeasurements()

    bill.forEach { billingPeriod ->
        var basicTotalCost = BigDecimal.ZERO
        var touTotalCost = BigDecimal.ZERO
        var totalKwh = BigDecimal.ZERO
        kwhEachHour
            .filter {
                it.time.isEqual(billingPeriod.startDate.atStartOfDay()) ||
                        it.time.isAfter(billingPeriod.startDate.atStartOfDay()) &&
                        it.time.isBefore(billingPeriod.endDate.atStartOfDay())
            }
            .forEach {
                basicTotalCost += BasicBilling.price(it.time) * it.kwh
                touTotalCost += TimeOfUseBilling.price(it.time) * it.kwh
                totalKwh += it.kwh
            }

        println("${totalKwh}kWh Basic: $${basicTotalCost.setScale(2, HALF_UP)} TOU: $${touTotalCost.setScale(2, HALF_UP)}")
    }
}

data class EnergyHour(
    val time: LocalDateTime,
    val kwh: BigDecimal
)

fun energyMeasurements(): List<EnergyHour> {
    val energyMeasurements = File("energy-measurements.csv").readLines()
        // Strip header row
        .drop(1)
        // Split to (timestamp, kwh for period)
        .map { it.split(",") }

    var priorEnergy = energyMeasurements[0][1].toBigDecimal()
    return energyMeasurements
        .mapNotNull { (timestamp, energy) ->
            val dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            if (energy != "null") {
                val thisPeriodEnergy = (energy.toBigDecimal() - priorEnergy)
                priorEnergy = energy.toBigDecimal()
                EnergyHour(dateTime, thisPeriodEnergy)
            } else {
                null
            }
        }
}
