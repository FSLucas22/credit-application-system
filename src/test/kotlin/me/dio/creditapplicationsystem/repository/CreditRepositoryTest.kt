package me.dio.creditapplicationsystem.repository

import me.dio.creditapplicationsystem.builders.buildCredit
import me.dio.creditapplicationsystem.builders.buildCustomer
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired lateinit var creditRepository: CreditRepository

    @Autowired lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var creditCode1: UUID
    private lateinit var creditCode2: UUID
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    @BeforeEach fun setup() {
        customer = testEntityManager.persist(buildCustomer(id=null))
        val creditCode1 = UUID.fromString("65becc00-9d9b-4080-a8d2-88bd154580f1")
        val creditCode2 = UUID.fromString("50f00e5d-1954-45a6-83bf-f2a9d91225a9")

        credit1 = testEntityManager.persist(
            buildCredit(
            id = null,
            creditCode = creditCode1,
            customer = customer
        )
        )
        credit2 = testEntityManager.persist(
            buildCredit(
            id = null,
            creditCode = creditCode2,
            customer = customer
        )
        )
    }

    @Test
    fun `Should find credit by creditCode`() {
        // Given
        // When
        val fakeCredit1: Credit = creditRepository.findByCreditCode(creditCode1)!!
        val fakeCredit2: Credit = creditRepository.findByCreditCode(creditCode2)!!

        // Then
        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)
    }

    @Test
    fun `Should not find credit with invalid credit code`() {
        // Given
        val creditCode = UUID.fromString("119b1eff-8d8d-4e24-8978-3e9bf8617763")

        // When
        val actual = creditRepository.findByCreditCode(creditCode)

        // Then
        Assertions.assertThat(actual).isNull()
    }

    @Test
    fun `Should find all credits by customer`() {
        // Given
        // When
        val credits = creditRepository.findAllByCustomer(customerId = customer.id!!)

        Assertions.assertThat(credits)
            .containsOnly(credit1, credit2)
    }

    @Test
    fun `Should not return credit of another customer`() {
        // Given
        val anotherCustomer = testEntityManager.persist(
            buildCustomer(
            id = null,
            cpf = "004.339.580-57",
            email = "another@test.com"
        )
        )
        val anotherCredit = testEntityManager.persist(buildCredit(id = null, customer = anotherCustomer))

        // When
        val credits = creditRepository.findAllByCustomer(customerId = customer.id!!)

        // Then
        Assertions.assertThat(credits).doesNotContain(anotherCredit)
    }

    @Test
    fun `Should not return credits of invalid customer`() {
        // Given
        val invalidCustomerId = -1L

        // When
        val credits = creditRepository.findAllByCustomer(invalidCustomerId)

        // Then
        Assertions.assertThat(credits).isEmpty()
    }
}