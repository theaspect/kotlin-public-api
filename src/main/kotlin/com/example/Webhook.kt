package com.example

import ai.api.model.Fulfillment
import ai.api.web.AIWebhookServlet
import javax.servlet.annotation.WebServlet

/**
 * See https://github.com/dialogflow/dialogflow-java-client/tree/master/web/servlet
 *
 * curl -H "Content-Type: application/json; charset=utf-8" --data '{"result":{"fulfillment":{"speech":"Some speech here"}}}' "http://localhost:8080/webhook"
 */
@WebServlet("/webhook")
class Webhook : AIWebhookServlet() {
    override fun doWebhook(input: AIWebhookRequest, output: Fulfillment) {
        output.speech = "You said: " + input.result.fulfillment.speech
    }

}
