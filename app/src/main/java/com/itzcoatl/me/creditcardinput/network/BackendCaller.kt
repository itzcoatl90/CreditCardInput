package com.itzcoatl.me.creditcardinput.network

import com.itzcoatl.me.creditcardinput.CreditCardPresenter
import com.itzcoatl.me.creditcardinput.model.CreditCardModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class BackendCaller(val presenter: CreditCardPresenter) {

    fun postToBackend(model: CreditCardModel) {
        // Testing Anko
        doAsync {
            val successful = makeNetworkCall(model)
            uiThread {
                if (successful) {
                    presenter.displaySuccessfulCreditCardStorage()
                }
            }
        }
    }

    private fun makeNetworkCall(model: CreditCardModel):  Boolean {
        // Simulating posting to server, remember to use https
        Thread.sleep(Random().nextInt(3).toLong() * 1000)
        // For simplicity, always successful
        return true
    }


}