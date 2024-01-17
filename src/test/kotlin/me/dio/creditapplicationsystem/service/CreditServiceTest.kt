package me.dio.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.enumeration.Status
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.service.impl.CreditServiceImpl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

// @ActiveProfiles("test") - Não necessário porque não precisa subir o contexto do spring
@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditServiceImpl

    @Test
    fun `Should create a new credit`() {
        // Given
        val fakeCredit = buildCredit()
        val fakeCustomer = fakeCredit.customer!!

        every { customerService.findById(fakeCustomer.id!!) } returns fakeCustomer
        every { creditRepository.save(fakeCredit) } returns fakeCredit

        // When
        val actual = creditService.save(fakeCredit)

        // Then
        Assertions.assertThat(actual)
            .isNotNull
            .isSameAs(fakeCredit)

        verify(exactly = 1) { customerService.findById(fakeCustomer.id!!) }
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `Should not create credit on invalid dayFirstInstallment and throw BusinessException`() {
        // Given
        val fakeCredit = buildCredit(dayFirstInstallment = LocalDate.now().plusMonths(4))
        val fakeCustomer = fakeCredit.customer!!

        every { customerService.findById(fakeCustomer.id!!) } returns fakeCustomer

        // When
        // Then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.save(fakeCredit) }
            .withMessage("Day of first installment must be in the next 3 months")

        verify(exactly = 1) { customerService.findById(fakeCustomer.id!!) }
        verify(exactly = 0) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `Should return all credits by customer`() {
        // Given
        val fakeCustomer = buildCustomer()
        val fakeCustomerId = fakeCustomer.id!!
        val fakeCredits = listOf(
            buildCredit(customer = fakeCustomer),
            buildCredit(customer = fakeCustomer),
        )

        every { creditRepository.findAllByCustomer(fakeCustomerId) } returns fakeCredits

        // When
        val actual = creditService.findAllByCustomer(fakeCustomerId)

        // Then
        Assertions.assertThat(actual).isSameAs(fakeCredits)
        verify(exactly = 1) { creditRepository.findAllByCustomer(fakeCustomerId) }
    }

    @Test
    fun `Should return credit by credit code`() {
        // Given
        val fakeCustomerId = Random.nextLong()
        val fakeCustomer = buildCustomer(id = fakeCustomerId)
        val fakeCreditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(creditCode = fakeCreditCode, customer = fakeCustomer)

        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit

        // When
        val actual = creditService.findByCreditCode(fakeCustomerId, fakeCreditCode)

        // Then
        Assertions.assertThat(actual).isSameAs(fakeCredit)
    }

    @Test
    fun `Should not return credit by invalid customer and throw BusinessException`() {
        // Given
        val fakeCustomerId = Random.nextLong()
        val anotherFakeCustomerId = fakeCustomerId + 1
        val fakeCreditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(
            creditCode = fakeCreditCode,
            customer = buildCustomer(id = anotherFakeCustomerId)
        )

        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit

        // When
        // Then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCreditCode) }
            .withMessage("Contact admin")
    }

    @Test
    fun `Should not return credit by invalid creditCode`() {
        // Given
        val anyCustomerId = Random.nextLong()
        val anyCreditCode = UUID.randomUUID()
        every { creditRepository.findByCreditCode(any()) } returns null

        // When
        // Then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(anyCustomerId, anyCreditCode) }
            .withMessage("Credit code $anyCreditCode not found")
    }

    fun buildCredit(
        id: Long = 1L,
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(10000.0),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 4,
        status: Status = Status.IN_PROGRESS,
        customer: Customer = buildCustomer()
    ) = Credit(
        id = id,
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer
    )

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