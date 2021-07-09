package net.dinomite.energy.dominiontou

import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val bill = File("billing.csv").readLines()
    // Strip header row
    .drop(1)
    // Split to (start, end, kwh, cost, $/kwh)
    .map { it.split(",") }
    .map { (start, end, kwh, cost, dollarsPerKwh) ->
    }

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
