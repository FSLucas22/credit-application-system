package me.dio.creditapplicationsystem.builders

import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.enumeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

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

fun buildCreditDto(
    creditValue: BigDecimal = BigDecimal.valueOf(10000.0),
    dayFirstInstallment: LocalDate = LocalDate.now(),
    numberOfInstallments: Int = Random.nextInt(0, 48),
    customerId: Long = Random.nextLong(1, Long.MAX_VALUE)
) = CreditDto(
    creditValue = creditValue,
    dayFirstInstallment = dayFirstInstallment,
    numberOfInstallments = numberOfInstallments,
    customerId = customerId
)