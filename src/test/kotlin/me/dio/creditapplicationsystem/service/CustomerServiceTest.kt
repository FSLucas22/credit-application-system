package me.dio.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.service.impl.CustomerServiceImpl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

//@ActiveProfiles("test") - Não necessário pois não precisa subir o contexto do spring
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerServiceImpl

    @Test
    fun `Should create customer`() {
        // Given
        val fakeCustomer = buildCustomer()
        every { customerRepository.save(fakeCustomer) } returns fakeCustomer

        // When
        val actual: Customer = customerService.save(fakeCustomer)

        // Then
        Assertions.assertThat(actual)
            .isNotNull
            .isSameAs(fakeCustomer)

        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `Should find customer by id`() {
        // Given
        val fakeId = Random.nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)

        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)

        // When
        val actual: Customer = customerService.findById(fakeId)

        // Then
        Assertions.assertThat(actual)
            .isNotNull
            .isExactlyInstanceOf(Customer::class.java)

        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `Should not find customer by invalid id and throw BusinessException`() {
        // Given
        val fakeId = Random.nextLong()

        every { customerRepository.findById(fakeId) } returns Optional.empty()

        // When
        // Then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found")

        verify(exactly = 1) {
            customerRepository.findById(fakeId)
        }
    }

    @Test
    fun `Should delete customer by id`() {
        // Given
        val fakeId = Random.nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)

        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs

        // When
        customerService.delete(fakeId)

        // Then
        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    fun buildCustomer(
        firstName: String = "Lucas",
        lastName: String = "Santos",
        cpf: String = "578.821.400-90",
        email: String = "lucas@test.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua do Lucas",
        income: BigDecimal = BigDecimal.valueOf(10000.0),
        id: Long = 1L,
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(zipCode = zipCode, street = street),
        income = income,
        id = id,
    )
}