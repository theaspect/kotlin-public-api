package com.example

import ai.api.model.Fulfillment
import ai.api.web.AIWebhookServlet
import com.google.gson.Gson
import java.util.*
import javax.servlet.annotation.WebServlet

/**
 * See https://github.com/dialogflow/dialogflow-java-client/tree/master/web/servlet
 *
 * curl -H "Content-Type: application/json; charset=utf-8" --data '{"result":{"fulfillment":{"speech":"Some speech here"}}}' "http://localhost:5000/webhook"
 *
 * {
 *  "id":"fac4f622-b0f4-49ac-aa47-ff69dc556594",
 *  "timestamp":"Oct 14, 2017 9:56:41 AM",
 *  "lang":"en",
 *  "result":{
 *      "action":"insuranceSegmentHighlights",
 *      "score":1.0,
 *      "source":"agent",
 *      "parameters":{
 *          "insuranceSegment":"property and casualty"
 *      },
 *      "contexts":[],
 *      "metadata":{
 *          "intentName":"insurance-segment-highlights",
 *          "intentId":"97116750-b1ce-4072-bb3c-60e2196f4587",
 *          "webhookUsed":"true"
 *      },
 *      "resolvedQuery":"show me pc",
 *      "fulfillment":{
 *          "speech":"",
 *          "messages":[
 *              {"speech":[""],"type":"SPEECH","platform":"DEFAULT"}
 *          ]
 *      },
 *      "actionIncomplete":false
 *  },
 *  "status":{"code":200,"errorType":"success"},
 *  "sessionId":"da2313b2-4c9f-1647-6913-2af28fac01df"}
 */
@WebServlet("/webhook")
class Webhook : AIWebhookServlet() {
    private val random = Random()

    override fun doWebhook(input: AIWebhookRequest, output: Fulfillment) {
        System.out.println(Gson().toJson(input))
        val action = input.result.action
        output.speech = when(action) {
            "insuranceSegmentHighlights" -> {
                val segment = input.result.parameters["insuranceSegment"]

                val clients = random.nextInt(100)
                val premium = random.nextInt(1000)

                "In $segment you have $clients clients with $$premium premium"
            }
            "renewals" -> "You have Property Risk for Acme Incorporated in two days and Aviation Liability for Aero International in 7 days"
            "productHighlights" -> {
                val product = input.result.parameters["product"] ?: ""
                val productGroup = input.result.parameters["productGroup"] ?: ""

                val clients = random.nextInt(100)
                val premium = random.nextInt(1000)

                if (product != "" && productGroup != "") {
                    "In $productGroup in $product you have $clients clients with $$premium premium"
                } else if (product != "") {
                    "In $product you have $clients clients with $$premium million premium"
                } else if (productGroup != "") {
                    "In $productGroup you have $clients clients with $$premium million premium"
                } else {
                    "Please specify product and/or product group"
                }
            }
            "carrierForProduct" -> {
                val product = input.result.parameters["product"] ?: ""
                "For $product the best carriers are Global Insurance and Liability Incorporated"
            }
            "contactCEM" -> "You client engagement manager is Aaron A Aaronson I'll let him know"
            "carrierByClient" -> {
                val company = input.result.parameters["any"] ?: ""
                "Company $company written by American Liability"
            }
            "topClients" -> {
                val count = input.result.parameters["count"] ?: ""
                "You will receive top $count clients list on you email"
            }
            else -> {
                "I can't understand \"${input.result.resolvedQuery}\""
            }
        }
    }
}
