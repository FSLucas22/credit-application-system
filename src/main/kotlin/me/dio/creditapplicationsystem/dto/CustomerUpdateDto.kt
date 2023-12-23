package me.dio.creditapplicationsystem.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDto (
    @field:NotEmpty(message = "firstName cannot be empty") val firstName: String,
    @field:NotEmpty(message = "lastName cannot be empty") val lastName: String,
    @field:NotNull(message = "income cannot be null") val income: BigDecimal,
    @field:NotEmpty(message = "zipCode cannot be empty") val zipCode: String,
    @field:NotEmpty(message = "street cannot be empty") val street: String,
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = firstName
        customer.lastName = lastName
        customer.income = income
        customer.address = Address(zipCode = zipCode, street = street)

        return customer
    }
}
