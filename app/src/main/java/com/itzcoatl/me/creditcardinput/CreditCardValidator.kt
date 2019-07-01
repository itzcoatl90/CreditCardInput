package com.itzcoatl.me.creditcardinput

interface CreditCardValidator {
    fun verifyFirstName(firstName: String): Boolean
    fun verifyLastName(lastName: String): Boolean
    fun verifyCreditCardNumber(number: String): Boolean
    fun verifyExpirationDate(date: String): Boolean
    fun verifyCVV(cardType: CreditCardType, cvv: String): Boolean
}

enum class ValidationError {
    EMPTY_FIRST_NAME,
    SPECIAL_CHARACTERS_IN_FIRST_NAME,
    EMPTY_LAST_NAME,
    SPECIAL_CHARACTERS_IN_LAST_NAME,
    INVALID_CREDIT_CARD_LENGTH,
    CREDIT_CARD_NOT_SUPPORTED,
    INVALID_CREDIT_CARD_NUMBER,
    INVALID_EXPIRATION_DATE,
    INVALID_CVV
}

class CreditCardValidatorImpl (val creditCardPresenter: CreditCardPresenter) : CreditCardValidator {
    override fun verifyFirstName(firstName: String): Boolean {
        if (firstName == "") {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.EMPTY_FIRST_NAME)
            return false
        }
        if (!firstName.matches(Regex("[a-zA-Z ]+"))) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.SPECIAL_CHARACTERS_IN_FIRST_NAME)
            return false
        }
        return true
    }

    override fun verifyLastName(lastName: String): Boolean {
        if (lastName == "") {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.EMPTY_LAST_NAME)
            return false
        }
        if (!lastName.matches(Regex("[a-zA-Z ]+"))) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.SPECIAL_CHARACTERS_IN_LAST_NAME)
            return false
        }
        return true
    }

    override fun verifyCreditCardNumber(number: String): Boolean {
        if (!number.matches(Regex("\\d{15,19}"))) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.INVALID_CREDIT_CARD_LENGTH)
            return false
        }
        val cardType = getCardType(number)
        if (cardType == CreditCardType.NOT_SUPPORTED) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.CREDIT_CARD_NOT_SUPPORTED)
            return false
        } else {
            creditCardPresenter.displayCardType(cardType)
        }
        if (checksum(number) % 10 != 0) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.INVALID_CREDIT_CARD_NUMBER)
            return false
        }
        return true
    }

    override fun verifyExpirationDate(date: String): Boolean {
        val tokens = date.split("/")
        if (!date.matches(Regex("\\d{1,2}/\\d{2}")) ||
                tokens.size != 2 ||
                !isMonth(tokens[0]) ||
                !isYear(tokens[1])) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.INVALID_EXPIRATION_DATE)
            return false
        }
        return true
    }

    override fun verifyCVV(cardType: CreditCardType, cvv: String): Boolean {
        if (cardType == CreditCardType.AMERICAN_EXPRESS && cvv.length != 4) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.INVALID_CVV)
            return false
        } else if (cardType != CreditCardType.NOT_SUPPORTED &&
                cardType != CreditCardType.AMERICAN_EXPRESS &&
                cvv.length != 3) {
            creditCardPresenter.displayCreditCardValidationError(ValidationError.INVALID_CVV)
            return false
        }
        return true
    }

    private fun checksum(input: String) = addends(input).sum()

    private fun addends(input: String) = input.digits().mapIndexed { i, j ->
        when {
            (input.length - i + 1) % 2 == 0 -> j
            j >= 5 -> j * 2 - 9
            else -> j * 2
        }
    }

    private fun getCardType(number: String): CreditCardType {
        when {
            number.matches(Regex("3[4|7]\\d{13}")) ->
                return CreditCardType.AMERICAN_EXPRESS
            number.matches(Regex("\\d{16}")) && inDiscoverRange(number.substring(0,6)) ->
                return CreditCardType.DISCOVER
            number.matches(Regex("\\d{16}")) && inMasterCardRange(number.substring(0,6)) ->
                return CreditCardType.MASTER_CARD
            number.matches(Regex("4\\d{15,18}")) ->
                return CreditCardType.VISA
        }
        return CreditCardType.NOT_SUPPORTED
    }

    private fun inDiscoverRange(prefix: String): Boolean {
        val prefixInt = prefix.toInt()
        if (
                prefixInt >= 601100 && prefixInt <= 601109 ||
                prefixInt >= 601120 && prefixInt <= 601149 ||
                prefixInt == 601174 ||
                prefixInt >= 601177 && prefixInt <= 601179 ||
                prefixInt >= 601186 && prefixInt <= 601199 ||
                prefixInt >= 644000 && prefixInt <= 659999
        ) { return true }
        return false
    }

    private fun inMasterCardRange(prefix: String): Boolean {
        val prefixInt = prefix.toInt()
        if (
                prefixInt >= 510000 && prefixInt <= 559999 ||
                prefixInt >= 222100 && prefixInt <= 272099
        ) { return true }
        return false
    }

    private fun isMonth(prefix: String): Boolean {
        val prefixInt = prefix.toInt()
        if (
                prefixInt >= 1 && prefixInt <= 12
        ) { return true }
        return false
    }

    private fun isYear(prefix: String): Boolean {
        val prefixInt = prefix.toInt()
        if (
                prefixInt >= 0 && prefixInt <= 99
        ) { return true }
        return false
    }

}

private fun String.matches(regex: Regex): Boolean {
    return regex.matches(this)
}

private fun String.digits(): List<Int> {
    return this.map(Character::getNumericValue)
}
