package me.dio.creditapplicationsystem.controller

import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.dto.CreditView
import me.dio.creditapplicationsystem.dto.CustomerCreditView
import me.dio.creditapplicationsystem.service.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService,
) {
    @PostMapping
    fun saveCredit(@RequestBody creditDto: CreditDto): ResponseEntity<String> {
        val credit = creditService.save(creditDto.toEntity())
        val customer = credit.customer
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!")
    }

    @GetMapping("/{customerId}")
    fun findAllCustomerCredits(
        @RequestParam customerId: Long
    ): ResponseEntity<List<CreditView>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(creditService
            .findAllByCustomer(customerId)
            .map { CreditView(it) })

    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @PathVariable creditCode: UUID,
        @RequestParam customerId: Long
    ): ResponseEntity<CustomerCreditView> = ResponseEntity
        .status(HttpStatus.OK)
        .body(
            CustomerCreditView(
                creditService.findByCreditCode(
                    creditCode = creditCode,
                    customerId = customerId,
                )
            )
        )
}