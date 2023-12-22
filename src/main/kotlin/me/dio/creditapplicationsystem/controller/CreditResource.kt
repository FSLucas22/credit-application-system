package me.dio.creditapplicationsystem.controller

import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.dto.CreditView
import me.dio.creditapplicationsystem.dto.CustomerCreditView
import me.dio.creditapplicationsystem.service.CreditService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService,
) {
    @PostMapping
    fun saveCredit(@RequestBody creditDto: CreditDto): String {
        val credit = creditService.save(creditDto.toEntity())
        val customer = credit.customer
        return "Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!"
    }

    @GetMapping("/{customerId}")
    fun findAllCustomerCredits(@RequestParam customerId: Long): List<CreditView> = creditService
        .findAllByCustomer(customerId)
        .map { CreditView(it) }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @PathVariable creditCode: UUID,
        @RequestParam customerId: Long
    ): CustomerCreditView = CustomerCreditView(
            creditService.findByCreditCode(
                creditCode = creditCode,
                customerId = customerId,
            )
        )
}