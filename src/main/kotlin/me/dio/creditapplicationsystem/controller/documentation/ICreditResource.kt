package me.dio.creditapplicationsystem.controller.documentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.dto.CreditView
import me.dio.creditapplicationsystem.dto.CustomerCreditView
import me.dio.creditapplicationsystem.dto.ListCreditView
import me.dio.creditapplicationsystem.exception.ExceptionDetails
import org.springframework.http.ResponseEntity
import java.util.*

interface ICreditResource {
    @Operation(summary = "Creates a new credit associated with a customer")
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "Credit created successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreditView::class),
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
    @Tag(name = "Credits", description = "Operations related to credit management")
    fun saveCredit(creditDto: CreditDto): ResponseEntity<CreditView>

    @Operation(summary = "Lista todos os créditos de um cliente")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Customer credits returned successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ListCreditView::class),
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
    @Tag(name = "Credits", description = "Operations related to credit management")
    fun findAllCustomerCredits(customerId: Long): ResponseEntity<ListCreditView>

    @Operation(summary = "Retorna as informações do crédito de um cliente")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Credit data returned successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CustomerCreditView::class),
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
    @Tag(name = "Credits", description = "Operations related to credit management")
    fun findByCreditCode(creditCode: UUID, customerId: Long): ResponseEntity<CustomerCreditView>
}