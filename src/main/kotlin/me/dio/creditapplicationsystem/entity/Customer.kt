package me.dio.creditapplicationsystem.entity

data class Customer(
    val id: Long? = null,
    var firstName: String = "",
    var lastName: String = "",
    val cpf: String,
    var email: String = "",
    var password: String = "",
    var address: Address = Address(),
    var credits: List<Credit> = mutableListOf()
)
