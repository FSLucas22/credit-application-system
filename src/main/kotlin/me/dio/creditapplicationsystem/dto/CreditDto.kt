package me.dio.creditapplicationsystem.dto

import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto (
    val creditValue: BigDecimal,
    val dayFirstInstallment: LocalDate,
    val numberOfStallments: Int,
    val customerId: Long,
) {
    fun toEntity(): Credit = Credit(
        creditValue = creditValue,
        dayFirstStallment = dayFirstInstallment,
        numberOfInstallments = numberOfStallments,
        customer = Customer(id = customerId)
    )
}
