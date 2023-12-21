package me.dio.creditapplicationsystem.controller

import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.service.CustomerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerResource(
    private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody customerDto: CustomerDto): String {
        val customer = customerService.save(customerDto.toEntity())
        return "Customer ${customer.email} saved"
    }
}