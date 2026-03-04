package com.minimarket.catalogtservice.domain

import java.math.BigDecimal

class Price(
    val original: BigDecimal,
    val current: BigDecimal
) {
    companion object {
        fun of(original: BigDecimal, current: BigDecimal?): Price {
            return Price(
                original = original,
                current = current?: original
            )
        }
    }
}