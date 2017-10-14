package com.example

import ai.api.model.Fulfillment
import ai.api.web.AIWebhookServlet
import com.google.gson.Gson
import javax.servlet.annotation.WebServlet

/**
 * See https://github.com/dialogflow/dialogflow-java-client/tree/master/web/servlet
 *
 * curl -H "Content-Type: application/json; charset=utf-8" --data '{"result":{"fulfillment":{"speech":"Some speech here"}}}' "http://localhost:5000/webhook"
 */
@WebServlet("/webhook")
class Webhook : AIWebhookServlet() {
    override fun doWebhook(input: AIWebhookRequest, output: Fulfillment) {
        System.out.println(Gson().toJson(input))
        output.displayText = "Echo text"
        output.speech = "Echo speech"
    }
}
