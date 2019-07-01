package com.itzcoatl.me.creditcardinput

import org.junit.Assert
import org.junit.Test

class CreditCardValidatorTest {
    val presenter: CreditCardPresenter
    val validator: CreditCardValidator
    var validationError: ValidationError? = null
    var creditCardType: CreditCardType? = null

    init {
        presenter = object: CreditCardPresenter {
            override fun displayPostingCreditCardToBackend() {
                // Do nothing, not part of test
            }

            override fun displayCardType(cardType: CreditCardType) {
                creditCardType = cardType
            }

            override fun displaySuccessfulCreditCardStorage() {
                // Do nothing, not part of test
            }

            override fun displayCreditCardValidationError(error: ValidationError) {
                validationError = error
            }
        }
        validator = CreditCardValidatorImpl(presenter)
    }



    @Test
    fun firstNameEmpty() {
        val validation = validator.verifyFirstName("")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.EMPTY_FIRST_NAME, validationError)
    }

    @Test
    fun firstNameSpecialChars() {
        val validation = validator.verifyFirstName("Testing!")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.SPECIAL_CHARACTERS_IN_FIRST_NAME, validationError)
    }

    @Test
    fun firstNameCorrect() {
        val validation = validator.verifyFirstName("John Or Jane")
        Assert.assertEquals(true, validation)
    }

    @Test
    fun lastNameEmpty() {
        val validation = validator.verifyLastName("")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.EMPTY_LAST_NAME, validationError)
    }

    @Test
    fun lastNameSpecialChars() {
        val validation = validator.verifyLastName("Testing!")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.SPECIAL_CHARACTERS_IN_LAST_NAME, validationError)
    }

    @Test
    fun lastNameCorrect() {
        val validation = validator.verifyLastName("Doe and Doe")
        Assert.assertEquals(true, validation)
    }

    @Test
    fun cardOutOfLength() {
        var validation = validator.verifyCreditCardNumber("1234567890")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.INVALID_CREDIT_CARD_LENGTH, validationError)
        validation = validator.verifyCreditCardNumber("12345678901234567890")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.INVALID_CREDIT_CARD_LENGTH, validationError)
    }

    @Test
    fun cardNotDigits() {
        val validation = validator.verifyCreditCardNumber("1234567890ABCDEF")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.INVALID_CREDIT_CARD_LENGTH, validationError)
    }

    @Test
    fun cardNotSupported() {
        val validation = validator.verifyCreditCardNumber("3528123456789012")
        Assert.assertEquals(false, validation)
        Assert.assertEquals(ValidationError.CREDIT_CARD_NOT_SUPPORTED, validationError)
    }

    @Test
    fun cardTypes() {
        validator.verifyCreditCardNumber("341234567890123")
        Assert.assertEquals(CreditCardType.AMERICAN_EXPRESS, creditCardType)
        validator.verifyCreditCardNumber("371234567890123")
        Assert.assertEquals(CreditCardType.AMERICAN_EXPRESS, creditCardType)
        validator.verifyCreditCardNumber("6011021234567890")
        Assert.assertEquals(CreditCardType.DISCOVER, creditCardType)
        validator.verifyCreditCardNumber("6011221234567890")
        Assert.assertEquals(CreditCardType.DISCOVER, creditCardType)
        validator.verifyCreditCardNumber("6011781234567890")
        Assert.assertEquals(CreditCardType.DISCOVER, creditCardType)
        validator.verifyCreditCardNumber("6011741234567890")
        Assert.assertEquals(CreditCardType.DISCOVER, creditCardType)
        validator.verifyCreditCardNumber("6011871234567890")
        Assert.assertEquals(CreditCardType.DISCOVER, creditCardType)
        validator.verifyCreditCardNumber("6440001234567890")
        Assert.assertEquals(CreditCardType.DISCOVER, creditCardType)
        validator.verifyCreditCardNumber("5100011234567890")
        Assert.assertEquals(CreditCardType.MASTER_CARD, creditCardType)
        validator.verifyCreditCardNumber("2221001234567890")
        Assert.assertEquals(CreditCardType.MASTER_CARD, creditCardType)
        validator.verifyCreditCardNumber("4123456789012345678")
        Assert.assertEquals(CreditCardType.VISA, creditCardType)
        validator.verifyCreditCardNumber("412345678901234567")
        Assert.assertEquals(CreditCardType.VISA, creditCardType)
        validator.verifyCreditCardNumber("41234567890123456")
        Assert.assertEquals(CreditCardType.VISA, creditCardType)
        validator.verifyCreditCardNumber("4123456789012345")
        Assert.assertEquals(CreditCardType.VISA, creditCardType)
    }

    @Test
    fun cardLuhnValidation() {
        var validation = validator.verifyCreditCardNumber("1234567890123456")
        Assert.assertEquals(false, validation)
        validation = validator.verifyCreditCardNumber("371449635398431")
        Assert.assertEquals(true, validation)
        validation = validator.verifyCreditCardNumber("1234567890123456")
        Assert.assertEquals(false, validation)
        validation = validator.verifyCreditCardNumber("5555555555554444")
        Assert.assertEquals(true, validation)
        validation = validator.verifyCreditCardNumber("1234567890123456")
        Assert.assertEquals(false, validation)
        validation = validator.verifyCreditCardNumber("4111111111111111")
        Assert.assertEquals(true, validation)
    }

    @Test
    fun cardExpirationValidation() {
        var validation = validator.verifyExpirationDate("111/20")
        Assert.assertEquals(false, validation)
        validation = validator.verifyExpirationDate("10/20")
        Assert.assertEquals(true, validation)
        validation = validator.verifyExpirationDate("13/20")
        Assert.assertEquals(false, validation)
        validation = validator.verifyExpirationDate("1/20")
        Assert.assertEquals(true, validation)
        validation = validator.verifyExpirationDate("31/10/20")
        Assert.assertEquals(false, validation)
        validation = validator.verifyExpirationDate("10/2020")
        Assert.assertEquals(false, validation)
    }

    @Test
    fun cardCVVValidation() {
        var validation = validator.verifyCVV(CreditCardType.AMERICAN_EXPRESS, "123")
        Assert.assertEquals(false, validation)
        validation = validator.verifyCVV(CreditCardType.AMERICAN_EXPRESS, "1234")
        Assert.assertEquals(true, validation)
        validation = validator.verifyCVV(CreditCardType.VISA, "1234")
        Assert.assertEquals(false, validation)
        validation = validator.verifyCVV(CreditCardType.MASTER_CARD, "123")
        Assert.assertEquals(true, validation)
    }

}