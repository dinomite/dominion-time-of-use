package net.dinomite.energy.dominiontou

import java.math.BigDecimal
import java.time.LocalDateTime

interface EnergyPricer {
    fun price(datetime: LocalDateTime): BigDecimal
}
