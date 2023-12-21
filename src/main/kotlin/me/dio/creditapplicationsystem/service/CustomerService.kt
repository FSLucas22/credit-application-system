package me.dio.creditapplicationsystem.service

import me.dio.creditapplicationsystem.entity.Customer

interface CustomerService {
    fun save(customer: Customer): Customer
    fun findById(customerId: Long): Customer
    fun delete(id: Long)
}