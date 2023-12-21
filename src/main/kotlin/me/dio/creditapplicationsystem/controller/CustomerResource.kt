package me.dio.creditapplicationsystem.controller

import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.dto.CustomerUpdateDto
import me.dio.creditapplicationsystem.dto.CustomerView
import me.dio.creditapplicationsystem.service.CustomerService
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): CustomerView = CustomerView(customerService.findById(id))

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long) = customerService.delete(id)

    @PatchMapping
    fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,
        @RequestBody customerUpdateDto: CustomerUpdateDto
    ): CustomerView {
        val customer = customerService.findById(id)
        val updatedCustomer = customerUpdateDto.toEntity(customer)
        return CustomerView(customerService.save(updatedCustomer))
    }
}