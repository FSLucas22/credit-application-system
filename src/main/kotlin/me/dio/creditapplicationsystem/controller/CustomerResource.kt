package me.dio.creditapplicationsystem.controller

import jakarta.validation.Valid
import me.dio.creditapplicationsystem.controller.documentation.ICustomerResource
import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.dto.CustomerUpdateDto
import me.dio.creditapplicationsystem.dto.CustomerView
import me.dio.creditapplicationsystem.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
class CustomerResource(
    private val customerService: CustomerService
): ICustomerResource {
    @PostMapping
    override fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<String> {
        val customer = customerService.save(customerDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Customer ${customer.email} saved")
    }

    @GetMapping("/{id}")
    override fun findById(
        @PathVariable id: Long
    ): ResponseEntity<CustomerView> = ResponseEntity
        .status(HttpStatus.OK)
        .body(CustomerView(customerService.findById(id)))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun deleteCustomer(@PathVariable id: Long) = customerService.delete(id)

    @PatchMapping
    override fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,
        @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {
        val customer = customerService.findById(id)
        val updatedCustomer = customerUpdateDto.toEntity(customer)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomerView(customerService.save(updatedCustomer)))
    }
}