package me.dio.creditapplicationsystem

import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.enumeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

fun buildCredit(
    id: Long? = 1L,
    creditCode: UUID = UUID.randomUUID(),
    creditValue: BigDecimal = BigDecimal.valueOf(10000.0),
    dayFirstInstallment: LocalDate = LocalDate.now(),
    numberOfInstallments: Int = 4,
    status: Status = Status.IN_PROGRESS,
    customer: Customer = buildCustomer()
) = Credit(
    id = id,
    creditCode = creditCode,
    creditValue = creditValue,
    dayFirstInstallment = dayFirstInstallment,
    numberOfInstallments = numberOfInstallments,
    status = status,
    customer = customer
)

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