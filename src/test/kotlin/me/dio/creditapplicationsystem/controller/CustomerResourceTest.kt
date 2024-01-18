package me.dio.creditapplicationsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.creditapplicationsystem.buildCustomer
import me.dio.creditapplicationsystem.buildCustomerDto
import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.dto.CustomerUpdateDto
import me.dio.creditapplicationsystem.repository.CustomerRepository
import org.assertj.core.api.Assertions
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
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `Should create a customer and return 201 status`() {
        // Given
        val customerDto: CustomerDto = buildCustomerDto()
        val requestData = objectMapper.writeValueAsString(customerDto)

        // When
        val response = mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(MockMvcResultHandlers.print())
            .andReturn().response.contentAsString

        Assertions.assertThat(response).isEqualTo("Customer ${customerDto.email} saved")
    }

    @Test
    fun `Should not create customer with repeated CPF return 409 status`() {
        // Given
        val firstCustomerDto: CustomerDto = buildCustomerDto(email = "lucas@test.com")
        customerRepository.save(firstCustomerDto.toEntity())

        val secondCustomerDto: CustomerDto = buildCustomerDto(
            cpf = firstCustomerDto.cpf,
            email = "lucas2@test.com"
        )

        val requestData = objectMapper.writeValueAsString(secondCustomerDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp")
                .exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.dao.DataIntegrityViolationException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create customer with repeated email return 409 status`() {
        // Given
        val firstCustomerDto: CustomerDto = buildCustomerDto(cpf = "836.157.550-22")
        customerRepository.save(firstCustomerDto.toEntity())

        val secondCustomerDto: CustomerDto = buildCustomerDto(
            cpf = "588.265.730-00",
            email = firstCustomerDto.email
        )

        val requestData = objectMapper.writeValueAsString(secondCustomerDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp")
                .exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.dao.DataIntegrityViolationException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create customer with empty fields and return 400 status`() {
        // Given
        val invalidCustomerDto: CustomerDto = buildCustomerDto(
            firstName = "",
            lastName = "",
            email = "",
            password = "",
        )

        val requestData = objectMapper.writeValueAsString(invalidCustomerDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp")
                .exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.firstName")
                .value("firstName cannot be empty"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.lastName")
                .value("lastName cannot be empty"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.email")
                .value("email cannot be empty"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.password")
                .value("password cannot be empty"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should find customer by id and return 200 status`() {
        // Given
        val customer = customerRepository.save(buildCustomer(id = null))

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                .value(customer.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName")
                .value(customer.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName")
                .value(customer.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf")
                .value(customer.cpf))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                .value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode")
                .value(customer.address.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street")
                .value(customer.address.street))
    }

    @Test
    fun `Should not find customer with invalid id and return 400 status`() {
        // Given
        val invalidCustomerId = Random.nextLong()

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${invalidCustomerId}")
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
    fun `Should delete customer by id`() {
        // Given
        val customer = buildCustomer(id = null)
        customerRepository.save(customer)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .delete("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not delete customer with invalid id and return 400 status`() {
        // Given
        val invalidCustomerId = Random.nextLong()

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .delete("$URL/${invalidCustomerId}")
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
    fun `Should update a customer and return 200 status`() {
        // Given
        val customerDto: CustomerDto = buildCustomerDto()
        val customer = customerRepository.save(customerDto.toEntity())

        val updateCustomerDto = CustomerUpdateDto(
            firstName = "Lucas",
            lastName = "Ferreira",
            income = BigDecimal.valueOf(5000000.0),
            zipCode = "00000",
            street = "Rua Ferreira",
        )
        val requestData = objectMapper.writeValueAsString(updateCustomerDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                .value(customer.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName")
                .value(updateCustomerDto.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName")
                .value(updateCustomerDto.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode")
                .value(updateCustomerDto.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street")
                .value(updateCustomerDto.street))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income")
                .value(updateCustomerDto.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not update a customer by invalid id and return 400 status`() {
        // Given
        val invalidCustomerId = Random.nextLong()

        val updateCustomerDto = CustomerUpdateDto(
            firstName = "Lucas",
            lastName = "Ferreira",
            income = BigDecimal.valueOf(5000000.0),
            zipCode = "00000",
            street = "Rua Ferreira",
        )
        val requestData = objectMapper.writeValueAsString(updateCustomerDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .patch("$URL?customerId=${invalidCustomerId}")
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
    fun `Should not update a customer with empty fields return 400 status`() {
        // Given
        val customerDto: CustomerDto = buildCustomerDto()
        val customer = customerRepository.save(customerDto.toEntity())

        val updateCustomerDto = CustomerUpdateDto(
            firstName = "",
            lastName = "",
            income = BigDecimal.valueOf(5000000.0),
            zipCode = "",
            street = "",
        )
        val requestData = objectMapper.writeValueAsString(updateCustomerDto)

        // When
        mockMvc.perform(MockMvcRequestBuilders
            .patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestData)
        ) // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp")
                .exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.firstName")
                .value("firstName cannot be empty"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.lastName")
                .value("lastName cannot be empty"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.zipCode")
                .value("zipCode cannot be empty"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.street")
                .value("street cannot be empty"))
            .andDo(MockMvcResultHandlers.print())
    }
}