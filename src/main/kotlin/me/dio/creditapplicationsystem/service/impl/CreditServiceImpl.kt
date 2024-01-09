package me.dio.creditapplicationsystem.service.impl

import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.service.CreditService
import me.dio.creditapplicationsystem.service.CustomerService
import org.springframework.stereotype.Service
import java.time.LocalDate
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
        
        validateDateFirstInstallment(credit.dayFirstInstallment)
        return creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> = creditRepository
        .findAllByCustomer(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit = creditRepository
            .findByCreditCode(creditCode)
            ?: throw BusinessException("Credit code $creditCode not found")

        return if (credit.customer?.id == customerId) credit
            else throw BusinessException("Contact admin")
    }

    fun validateDateFirstInstallment(dateFirstInstallment: LocalDate) = if (
            dateFirstInstallment.isBefore(LocalDate.now().plusMonths(3))
        ) true else throw BusinessException("Day of first instalmment must be in the next 3 months")
}