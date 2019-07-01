package com.itzcoatl.me.creditcardinput

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.view.View
import com.itzcoatl.me.creditcardinput.model.CreditCardModel
import com.itzcoatl.me.creditcardinput.network.BackendCaller
import kotlinx.android.synthetic.main.activity_credit_card_form.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog



class CreditCardFormActivity : AppCompatActivity(), CreditCardPresenter {

    val creditCardValidator = CreditCardValidatorImpl(this)
    val backendCaller = BackendCaller(this)
    var creditCardType: CreditCardType? = null
    var submitting = false

    companion object {
        const val FIRST_NAME = "FIRST_NAME"
        const val LAST_NAME = "FIRST_NAME"
        const val CREDIT_CARD = "CREDIT_CARD"
        const val EXPIRATION_DATE = "EXPIRATION_DATE"
        const val CVV = "CVV"
    }

    // App Compat Activity functions
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString(FIRST_NAME,
                credit_card_user_first_name.text.toString())
        savedInstanceState.putString(LAST_NAME,
                credit_card_user_last_name.text.toString())
        savedInstanceState.putString(CREDIT_CARD,
                credit_card_number.text.toString())
        savedInstanceState.putString(EXPIRATION_DATE,
                credit_card_expiration.text.toString())
        savedInstanceState.putString(CVV,
                credit_card_cvv.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card_form)

        savedInstanceState?.let {
            restoreSavedInstanceState(it)
        }
        addChangeListeners()
        credit_card_submit.setOnClickListener {
            if (submitting) {
                return@setOnClickListener
            }
            val firstNameCorrect =
                    creditCardValidator.verifyFirstName(
                            credit_card_user_first_name.text.toString()
                    )
            val lastNameCorrect =
                    creditCardValidator.verifyLastName(
                            credit_card_user_last_name.text.toString()
                    )
            val numberCorrect =
                    creditCardValidator.verifyCreditCardNumber(
                            credit_card_number.text.toString()
                    )
            val expirationDateCorrect =
                    creditCardValidator.verifyExpirationDate(
                            credit_card_expiration.text.toString()
                    )
            var cvvCorrect = false
            creditCardType?.let {
                cvvCorrect =
                        creditCardValidator.verifyCVV(it,
                                credit_card_cvv.text.toString())
            }
            if (firstNameCorrect &&
                    lastNameCorrect &&
                    numberCorrect &&
                    expirationDateCorrect &&
                    cvvCorrect) {
                val creditCardModel = CreditCardModel(
                        credit_card_user_first_name.text.toString(),
                        credit_card_user_last_name.text.toString(),
                        credit_card_number.text.toString(),
                        credit_card_expiration.text.toString(),
                        credit_card_cvv.text.toString().toInt()
                )
                displayPostingCreditCardToBackend()
                backendCaller.postToBackend(creditCardModel)
            }
        }
    }

    // Credit Card Presenter functions
    override fun displaySuccessfulCreditCardStorage() {
        submitting = false
        credit_card_submit.text =
                resources.getString(R.string.submit)
        AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.successful_title))
                .setMessage(resources.getString(R.string.successful_description))
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    // Do nothing
                })
                .setIcon(R.drawable.ic_check_circle_green)
                .show()
    }

    override fun displayPostingCreditCardToBackend() {
        submitting = true
        credit_card_submit.text =
                resources.getString(R.string.submitting)
    }

    override fun displayCreditCardValidationError(error: ValidationError) {
        when (error) {
            ValidationError.EMPTY_FIRST_NAME -> {
                credit_card_error_name.visibility = View.VISIBLE
                credit_card_error_name.text =
                        resources.getString(R.string.error_empty_first_name)
            }
            ValidationError.EMPTY_LAST_NAME -> {
                credit_card_error_name.visibility = View.VISIBLE
                credit_card_error_name.text =
                        resources.getString(R.string.error_empty_last_name)
            }
            ValidationError.SPECIAL_CHARACTERS_IN_FIRST_NAME -> {
                credit_card_error_name.visibility = View.VISIBLE
                credit_card_error_name.text =
                        resources.getString(R.string.error_special_char_in_first_name)
            }
            ValidationError.SPECIAL_CHARACTERS_IN_LAST_NAME -> {
                credit_card_error_name.visibility = View.VISIBLE
                credit_card_error_name.text =
                        resources.getString(R.string.error_special_char_in_last_name)
            }
            ValidationError.INVALID_CREDIT_CARD_LENGTH -> {
                credit_card_error_number.visibility = View.VISIBLE
                credit_card_error_number.text =
                        resources.getString(R.string.error_invalid_cc_number_length)
            }
            ValidationError.CREDIT_CARD_NOT_SUPPORTED -> {
                credit_card_error_number.visibility = View.VISIBLE
                credit_card_error_number.text =
                        resources.getString(R.string.error_cc_not_supported)
            }
            ValidationError.INVALID_CREDIT_CARD_NUMBER -> {
                credit_card_error_number.visibility = View.VISIBLE
                credit_card_error_number.text =
                        resources.getString(R.string.error_invalid_cc_number_luhn)
            }
            ValidationError.INVALID_EXPIRATION_DATE -> {
                credit_card_error_date.visibility = View.VISIBLE
                credit_card_error_date.text =
                        resources.getString(R.string.error_invalid_expiration_date)
            }
            ValidationError.INVALID_CVV -> {
                credit_card_error_date.visibility = View.VISIBLE
                credit_card_error_date.text =
                        resources.getString(R.string.error_invalid_cvv)
            }
        }
    }

    override fun displayCardType(cardType: CreditCardType) {
        creditCardType = cardType
        credit_card_thumbnail.visibility = View.VISIBLE
        when (cardType) {
            CreditCardType.AMERICAN_EXPRESS ->
                credit_card_thumbnail.background =
                        ContextCompat.getDrawable(this, R.drawable.thumbnail_american)
            CreditCardType.DISCOVER ->
                credit_card_thumbnail.background =
                        ContextCompat.getDrawable(this, R.drawable.thumbnail_discover)
            CreditCardType.MASTER_CARD ->
                credit_card_thumbnail.background =
                        ContextCompat.getDrawable(this, R.drawable.thumbnail_mastercard)
            CreditCardType.VISA ->
                credit_card_thumbnail.background =
                        ContextCompat.getDrawable(this, R.drawable.thumbnail_visa)
            else -> {
                credit_card_thumbnail.background =
                        ContextCompat.getDrawable(this, R.drawable.ic_credit_card_grey)
                credit_card_thumbnail.visibility = View.INVISIBLE
            }
        }
    }

    //Private functions
    private fun restoreSavedInstanceState(it: Bundle) {
        credit_card_user_first_name.text =
                Editable.Factory.getInstance().newEditable(
                        it.getString(FIRST_NAME, "").toString())
        credit_card_user_last_name.text =
                Editable.Factory.getInstance().newEditable(
                        it.getString(LAST_NAME, "").toString())
        credit_card_number.text =
                Editable.Factory.getInstance().newEditable(
                        it.getString(CREDIT_CARD, "").toString())
        credit_card_expiration.text =
                Editable.Factory.getInstance().newEditable(
                        it.getString(EXPIRATION_DATE, "").toString())
        credit_card_cvv.text =
                Editable.Factory.getInstance().newEditable(
                        it.getString(CVV, "").toString())
    }

    private fun addChangeListeners() {
        credit_card_user_first_name.setOnFocusChangeListener {
            v, hasFocus ->
            if (!hasFocus) {
                creditCardValidator.verifyFirstName(credit_card_user_first_name.text.toString())
            } else {
                // Clean previous errors
                credit_card_error_name.text = resources.getString(R.string.default_error)
                credit_card_error_name.visibility = View.INVISIBLE
            }

        }

        credit_card_user_last_name.setOnFocusChangeListener {
            v, hasFocus ->
            if (!hasFocus) {
                creditCardValidator.verifyLastName(credit_card_user_last_name.text.toString())
            } else {
                // Clean previous errors
                credit_card_error_name.text = resources.getString(R.string.default_error)
                credit_card_error_name.visibility = View.INVISIBLE
            }
        }

        credit_card_number.setOnFocusChangeListener {
            v, hasFocus ->
            if (!hasFocus) {
                creditCardValidator.verifyCreditCardNumber(credit_card_number.text.toString())
            } else {
                // Clean previous errors
                credit_card_error_number.text = resources.getString(R.string.default_error)
                credit_card_error_number.visibility = View.INVISIBLE
            }
        }

        credit_card_expiration.setOnFocusChangeListener {
            v, hasFocus ->
            if (!hasFocus) {
                creditCardValidator.verifyExpirationDate(credit_card_expiration.text.toString())
            } else {
                // Clean previous errors
                credit_card_error_date.text = resources.getString(R.string.default_error)
                credit_card_error_date.visibility = View.INVISIBLE
            }
        }
    }

}
