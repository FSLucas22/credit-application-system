package me.dio.creditapplicationsystem.controller

import jakarta.validation.Valid
import me.dio.creditapplicationsystem.controller.documentation.ICreditResource
import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.dto.CreditView
import me.dio.creditapplicationsystem.dto.CustomerCreditView
import me.dio.creditapplicationsystem.dto.ListCreditView
import me.dio.creditapplicationsystem.service.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/credits")
class CreditResource (
    private val creditService: CreditService,
): ICreditResource {
    @PostMapping
    override fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<String> {
        val credit = creditService.save(creditDto.toEntity())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!")
    }

    @GetMapping
    override fun findAllCustomerCredits(
        @RequestParam(value = "customerId") customerId: Long
    ): ResponseEntity<ListCreditView> = ResponseEntity
        .status(HttpStatus.OK)
        .body(ListCreditView(creditService
            .findAllByCustomer(customerId)
            .map { CreditView(it) }))

    @GetMapping("/{creditCode}")
    override fun findByCreditCode(
        @PathVariable creditCode: UUID,
        @RequestParam(value = "customerId") customerId: Long
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