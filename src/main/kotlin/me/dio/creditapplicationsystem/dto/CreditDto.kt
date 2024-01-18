package me.dio.creditapplicationsystem.dto

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto (
    @field:NotNull(message = "creditValue cannot be null")
    @field:Positive(message = "creditValue must be a positive")
    val creditValue: BigDecimal,

    @field:FutureOrPresent(message = "dayFirstInstallment must be a future date")
    val dayFirstInstallment: LocalDate,

    @field:NotNull(message = "numberOfInstallments cannot be null")
    @field:Positive(message = "numberOfInstallments must be positive")
    @field:Max(value = 48, message = "numberOfInstallments cannot be bigger than 48")
    val numberOfInstallments: Int,

    @field:NotNull(message = "customerId cannot be nul")
    val customerId: Long,
) {
    fun toEntity(): Credit = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = Customer(id = customerId)
    )
}
