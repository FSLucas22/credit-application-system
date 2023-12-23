package me.dio.creditapplicationsystem.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto (
    @field:NotEmpty(message = "firstName cannot be empty") val firstName: String,
    @field:NotEmpty(message = "lastName cannot be empty") val lastName: String,

    @field:NotEmpty(message = "cpf cannot be empty")
    @field:CPF(message = "invalid cpf") val cpf: String,

    @field:NotNull(message = "income cannot be null")
    val income: BigDecimal,

    @field:NotEmpty(message = "email cannot be empty")
    @field:Email(message = "invalid email")
    val email: String,

    @field:NotEmpty(message = "password cannot be empty") val password: String,
    @field:NotEmpty(message = "zipCode cannot be empty") val zipCode: String,
    @field:NotEmpty(message = "street cannot be empty") val street: String,
) {
    fun toEntity(): Customer = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        income = income,
        email = email,
        password = password,
        address = Address(zipCode = zipCode, street = street)
    )
}