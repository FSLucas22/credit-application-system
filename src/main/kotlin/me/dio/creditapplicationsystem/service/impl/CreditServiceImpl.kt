package me.dio.creditapplicationsystem.service.impl

import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.service.CreditService
import me.dio.creditapplicationsystem.service.CustomerService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditServiceImpl(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
) : CreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> = creditRepository
        .findAllByCustomer(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit = creditRepository
            .findByCreditCode(creditCode)
            ?: throw RuntimeException("Credit code $creditCode not found")

        return if (credit.customer?.id == customerId) credit
            else throw RuntimeException("Contact admin")
    }
}