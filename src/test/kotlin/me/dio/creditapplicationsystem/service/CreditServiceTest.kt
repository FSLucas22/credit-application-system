package me.dio.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.creditapplicationsystem.builders.buildCredit
import me.dio.creditapplicationsystem.builders.buildCustomer
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.service.impl.CreditServiceImpl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

// @ActiveProfiles("test") - Não necessário porque não precisa subir o contexto do spring
@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @MockK
    lateinit var creditRepository: CreditRepository
    @MockK
    lateinit var customerService: CustomerService
    @InjectMockKs
    lateinit var creditService: CreditServiceImpl

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
        every { customerService.findById(fakeCustomer.id!!) } returns fakeCustomer

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
}