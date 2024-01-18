package me.dio.creditapplicationsystem.builders

import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Customer
import java.math.BigDecimal

fun buildCustomer(
    firstName: String = "Lucas",
    lastName: String = "Santos",
    cpf: String = "578.821.400-90",
    email: String = "lucas@test.com",
    password: String = "12345",
    zipCode: String = "12345",
    street: String = "Rua do Lucas",
    income: BigDecimal = BigDecimal.valueOf(10000.0),
    id: Long? = 1L,
) = Customer(
    firstName = firstName,
    lastName = lastName,
    cpf = cpf,
    email = email,
    password = password,
    address = Address(zipCode = zipCode, street = street),
    income = income,
    id = id,
)

fun buildCustomerDto(
    firstName: String = "Lucas",
    lastName: String = "Santos",
    cpf: String = "578.821.400-90",
    email: String = "lucas@test.com",
    password: String = "12345",
    zipCode: String = "12345",
    street: String = "Rua do Lucas",
    income: BigDecimal = BigDecimal.valueOf(10000.0),
) = CustomerDto(
    firstName = firstName,
    lastName = lastName,
    cpf = cpf,
    email = email,
    password = password,
    zipCode = zipCode,
    street = street,
    income = income,
)