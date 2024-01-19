package me.dio.creditapplicationsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.creditapplicationsystem.builders.buildCredit
import me.dio.creditapplicationsystem.builders.buildCreditDto
import me.dio.creditapplicationsystem.builders.buildCustomer
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired lateinit var creditRepository: CreditRepository
    @Autowired lateinit var customerRepository: CustomerRepository
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/credits"
    }

    @BeforeEach fun setup() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @AfterEach fun tearDown() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @Test
    fun `Should create credit and return 201 status`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))
        val creditDto = buildCreditDto(customerId = customer.id!!)
        val requestData = objectMapper.writeValueAsString(creditDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue")
                .value(creditDto.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments")
                .value(creditDto.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dayFirstInstallment")
                .value(creditDto.dayFirstInstallment.toString()))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create credit with invalid customerId and return 404 status`() {
        // Given
        val invalidCustomerId = Random.nextLong()
        val creditDto = buildCreditDto(customerId = invalidCustomerId)
        val requestData = objectMapper.writeValueAsString(creditDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.creditapplicationsystem.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create credit with invalid dayFirstInstallment and return 404 status`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))
        val creditDto = buildCreditDto(
            customerId = customer.id!!,
            dayFirstInstallment = LocalDate.now().plusMonths(4),
        )
        val requestData = objectMapper.writeValueAsString(creditDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.creditapplicationsystem.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.null")
                .value("Day of first installment must be in the next 3 months"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create credit with invalid fields and return 404 status`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))
        val creditDto = buildCreditDto(
            customerId = customer.id!!,
            dayFirstInstallment = LocalDate.now().minusDays(1),
            creditValue = BigDecimal.valueOf(-1000),
            numberOfInstallments = -1,
        )
        val requestData = objectMapper.writeValueAsString(creditDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.dayFirstInstallment")
                .value("dayFirstInstallment must be a future or present date"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.numberOfInstallments")
                .value("numberOfInstallments must be positive"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.creditValue")
                .value("creditValue must be positive"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create credit with numberOfInstallments bigger than 48 and return 404 status`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))
        val creditDto = buildCreditDto(
            customerId = customer.id!!,
            numberOfInstallments = 49,
        )
        val requestData = objectMapper.writeValueAsString(creditDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.numberOfInstallments")
                .value("numberOfInstallments cannot be bigger than 48"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should find all credits by customer id`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))
        creditRepository.save(
            buildCredit(
                id = null,
                customer = customer,
                numberOfInstallments = 3
            )
        )
        creditRepository.save(
            buildCredit(
                id = null,
                customer = customer,
                numberOfInstallments = 2
            )
        )

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL?customerId=${customer.id!!}")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.credits.length()")
                .value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.credits.[*].creditCode").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.credits.[*].creditValue").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.credits.[*].numberOfInstallments")
                .isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.credits.[*].dayFirstInstallment")
                .isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not find credits for invalid customer id`() {
        // Given
        val invalidCustomerId = Random.nextLong()

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL?customerId=$invalidCustomerId")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.creditapplicationsystem.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should find credit by credit code with valid customer id`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))
        val credit = creditRepository.save(buildCredit(id = null, customer = customer))

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${credit.creditCode}?customerId=${customer.id!!}")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode")
                .value(credit.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue")
                .value(credit.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments")
                .value(credit.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                .value(credit.status.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer")
                .value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer")
                .value(customer.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not find credit with invalid credit code`() {
        // Given
        val invalidCreditCode = UUID.randomUUID()
        val invalidCustomerId = Random.nextLong()

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${invalidCreditCode}?customerId=${invalidCustomerId}")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
            .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.creditapplicationsystem.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not find credit with invalid customer id`() {
        // Given
        val validCustomer = customerRepository.save(buildCustomer(id = null))
        val credit = creditRepository.save(buildCredit(id = null, customer = validCustomer))
        val invalidCustomerId = validCustomer.id!! + 1

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${credit.creditCode}?customerId=${invalidCustomerId}")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.creditapplicationsystem.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.null")
                .value("Contact admin"))
            .andDo(MockMvcResultHandlers.print())
    }
}