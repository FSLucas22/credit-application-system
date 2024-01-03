package me.dio.creditapplicationsystem.service.impl

import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.service.CustomerService
import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
): CustomerService {
    override fun save(customer: Customer): Customer = customerRepository.save(customer)

    override fun findById(customerId: Long): Customer = customerRepository
        .findById(customerId).orElseThrow {
            BusinessException("Id $customerId not found")
        }

    override fun delete(id: Long) {
        with(findById(id), customerRepository::delete)
    }
}