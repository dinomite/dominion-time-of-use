package net.dinomite.energy.dominiontou

import net.dinomite.energy.dominiontou.Season.SUMMER
import net.dinomite.energy.dominiontou.Season.WINTER
import net.dinomite.energy.dominiontou.SeasonalPrice.PriceGroup
import net.dinomite.energy.dominiontou.SeasonalPrice.PriceGroup.Period
import net.dinomite.energy.dominiontou.SeasonalPrice.PriceGroup.PricePeriod
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

enum class Season(val months: Set<Int>) {
    SUMMER(listOf(5..9).flatten().toSet()),
    WINTER((listOf(1..4) + listOf(10..12)).flatten().toSet());

    fun month(num: Int) = if (SUMMER.months.contains(num)) SUMMER else WINTER
}

class SeasonalPrice(
    val weekday: PriceGroup,
    val weekend: PriceGroup
) {
    // TODO Ensure all days are covered
    class PriceGroup(
        val superOffPeak: PricePeriod,
        val offPeak: PricePeriod,
        val onPeak: PricePeriod? = null
    ) {
        // TODO Ensure all periods covered
        class Period(
            val start: LocalTime,
            val end: LocalTime
        ) {
            constructor(startHour: Int, endHour: Int) :
                    this(LocalTime.of(startHour, 0), LocalTime.of(endHour, 0))
            constructor(startHour: Int) : this(LocalTime.of(startHour, 0), LocalTime.MAX)
        }

        class PricePeriod(
            val periods: List<Period>,
            val price: BigDecimal
        ) {
            constructor(period: Period, price: BigDecimal) : this(listOf(period), price)
        }
    }
}

val pricing = mapOf(
    SUMMER to SeasonalPrice(
        weekday = PriceGroup(
            superOffPeak = PricePeriod(listOf(Period(0, 5)), BigDecimal(0.077139)),
            offPeak = PricePeriod(listOf(Period(5, 15), Period(18)), BigDecimal(0.095826)),
            onPeak = PricePeriod(Period(15, 18), BigDecimal(0.229038))
        ),
        weekend = PriceGroup(
            superOffPeak = PricePeriod(listOf(Period(0, 5)), BigDecimal(0.073712)),
            offPeak = PricePeriod(Period(5), BigDecimal(0.092399)),
        )
    ),
    WINTER to SeasonalPrice(
        weekday = PriceGroup(
            superOffPeak = PricePeriod(listOf(Period(0, 5)), BigDecimal(0.099736)),
            offPeak = PricePeriod(listOf(Period(5, 6), Period(9, 17), Period(20)), BigDecimal(0.103199)),
            onPeak = PricePeriod(listOf(Period(6, 9), Period(17, 20)), BigDecimal(0.174449))
        ),
        weekend = PriceGroup(
            superOffPeak = PricePeriod(listOf(Period(0, 5)), BigDecimal(0.099772)),
            offPeak = PricePeriod(Period(5), BigDecimal(0.096309)),
        )
    ),
)

fun price(time: LocalDateTime) {
    TODO("Return the price for the given time")
}

fun main() {
    // Energy measure at time; strip header row, split to (timestamp, energy)
    val energyMeasurements = File("energy-measurements.csv").readLines()
        .drop(1)
        .map { it.split(",") }

    var priorEnergy = energyMeasurements[0][1].toBigDecimal()
    val kwhEachHour = energyMeasurements
        .mapNotNull { (timestamp, energy) ->
            val dateTime = Instant.ofEpochSecond(timestamp.toLong())
                .atZone(ZoneId.of("America/New_York"))
                .toLocalDateTime()

            if (energy != "null") {
                val thisPeriodEnergy = (energy.toBigDecimal() - priorEnergy)
                priorEnergy = energy.toBigDecimal()
                dateTime to thisPeriodEnergy
            } else {
                null
            }
        }

    // TODO calculate bill for each billing period

    kwhEachHour
        .take(5)
        .forEach { println(it) }
    println()
    kwhEachHour
        .takeLast(5)
        .forEach { println(it) }
}
