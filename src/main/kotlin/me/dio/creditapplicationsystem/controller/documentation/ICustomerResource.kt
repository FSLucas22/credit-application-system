package me.dio.creditapplicationsystem.controller.documentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.dio.creditapplicationsystem.dto.CustomerCreditView
import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.dto.CustomerUpdateDto
import me.dio.creditapplicationsystem.dto.CustomerView
import me.dio.creditapplicationsystem.exception.ExceptionDetails
import org.springframework.http.ResponseEntity

interface ICustomerResource {
    @Operation(summary = "Returns customer data")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Customer info returned successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CustomerView::class),
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ExceptionDetails::class)
                )
            ]
        )
    )
    @Tag(name = "Customer", description = "Operations related to customer management")
    fun findById(id: Long): ResponseEntity<CustomerView>

    @Operation(summary = "Deletes a customer and all of his credits")
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "Customer deleted"
        ),
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ExceptionDetails::class)
                )
            ]
        )
    )
    @Tag(name = "Customer", description = "Operations related to customer management")
    fun deleteCustomer(id: Long)

    @Operation(summary = "Creates a new  customer")
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "Customer created",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = String::class),
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ExceptionDetails::class)
                )
            ]
        )
    )
    @Tag(name = "Customer", description = "Operations related to customer management")
    fun saveCustomer(customerDto: CustomerDto): ResponseEntity<String>

    @Operation(summary = "Updates customer data")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Customer data updated",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CustomerView::class),
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ExceptionDetails::class)
                )
            ]
        )
    )
    @Tag(name = "Customer", description = "Operations related to customer management")
    fun updateCustomer(id: Long, customerUpdateDto: CustomerUpdateDto): ResponseEntity<CustomerView>
}