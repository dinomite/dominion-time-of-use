package net.dinomite.energy.dominiontou

import net.dinomite.energy.dominiontou.TimeOfUseBilling.Season.SUMMER
import net.dinomite.energy.dominiontou.TimeOfUseBilling.Season.WINTER
import net.dinomite.energy.dominiontou.TimeOfUseBilling.SeasonalPrice.PriceGroup
import net.dinomite.energy.dominiontou.TimeOfUseBilling.SeasonalPrice.PriceGroup.Period
import net.dinomite.energy.dominiontou.TimeOfUseBilling.SeasonalPrice.PriceGroup.PricePeriod
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

object TimeOfUseBilling : EnergyPricer {
    enum class Season(private val months: Set<Int>) {
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

    // Time of use pricing https://www.dominionenergy.com/tou
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
                offPeak = PricePeriod(
                    listOf(Period(5, 6), Period(9, 17), Period(20)),
                    BigDecimal(0.103199)
                ),
                onPeak = PricePeriod(
                    listOf(Period(6, 9), Period(17, 20)),
                    BigDecimal(0.174449)
                )
            ),
            weekend = PriceGroup(
                superOffPeak = PricePeriod(listOf(Period(0, 5)), BigDecimal(0.099772)),
                offPeak = PricePeriod(Period(5), BigDecimal(0.096309))
            )
        )
    )

    override fun price(time: LocalDateTime): BigDecimal {
        TODO("Return the price for the given time")
    }
}
