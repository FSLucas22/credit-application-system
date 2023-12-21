package me.dio.creditapplicationsystem.service

import me.dio.creditapplicationsystem.entity.Credit
import java.util.*

interface CreditService {
    fun save(credit: Credit): Credit
    fun findAllByCustomer(customerId: Long): List<Credit>
    fun findByCreditCode(creditCode: UUID): Credit
}