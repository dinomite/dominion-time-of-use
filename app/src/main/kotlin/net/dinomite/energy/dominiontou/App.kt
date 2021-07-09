package net.dinomite.energy.dominiontou

import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
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

    kwhEachHour
        .take(5)
        .forEach { println(it) }
    println()
    kwhEachHour
        .takeLast(5)
        .forEach { println(it) }
    println()

    println("Basic billing")

    bill.forEach { billingPeriod ->
        var totalCost = BigDecimal.ZERO
        var totalKwh = BigDecimal.ZERO
        kwhEachHour.filter {
            it.time.isEqual(billingPeriod.startDate.atStartOfDay()) ||
                    it.time.isAfter(billingPeriod.startDate.atStartOfDay()) &&
                    it.time.isBefore(billingPeriod.endDate.atStartOfDay())
        }
            .forEach {
                val cost = BasicBilling.price(it.time) * it.kwh
                totalCost += cost
                totalKwh += it.kwh
            }

        println("${totalKwh}kWh $${totalCost.setScale(2, RoundingMode.HALF_UP)}")
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
