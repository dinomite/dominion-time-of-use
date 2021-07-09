package net.dinomite.energy.dominiontou

import java.io.File
import java.time.Instant
import java.time.ZoneId

fun main() {
    // Energy measure at time; strip header row, split to (timestamp, energy)
    val energyMeasurements = File("energy-measurements.csv").readLines()
        .drop(1)
        .map { it.split(",") }

    var priorEnergy = energyMeasurements[0][1].toDouble()
    val kwhEachHour = energyMeasurements
        .mapNotNull { (timestamp, energy) ->
            val dateTime = Instant.ofEpochSecond(timestamp.toLong())
                .atZone(ZoneId.of("America/New_York"))
                .toLocalDateTime()

            if (energy != "null") {
                val thisPeriodEnergy = (energy.toDouble() - priorEnergy)
                priorEnergy = energy.toDouble()
                dateTime to thisPeriodEnergy
            } else {
                null
            }
        }

    println(kwhEachHour.last())
}
