package com.itzcoatl.me.creditcardinput.model

data class CreditCardModel(
        val firstName: String,
        val lastName: String,
        val ccn: String,
        val expirationDate: String,
        val cvv: Int)