package com.itzcoatl.me.creditcardinput

interface CreditCardPresenter {
    fun displayCardType(cardType: CreditCardType)
    fun displaySuccessfulCreditCardStorage()
    fun displayPostingCreditCardToBackend()
    fun displayCreditCardValidationError(error: ValidationError)
}

enum class CreditCardType {
    AMERICAN_EXPRESS,
    DISCOVER,
    MASTER_CARD,
    VISA,
    NOT_SUPPORTED
}
