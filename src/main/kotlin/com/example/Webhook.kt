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
 *
 *  // TODO extract methods
 *  // TODO put context
 *  // TODO put fallback
 *  // TODO generate random clients
 *  // TODO generate random carriers
 *  // TODO generate random products
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

                "In $segment you have $clients clients with $$premium million premium"
            }
            "renewals" -> "You have Property Risk for Acme Incorporated in two days and Aviation Liability for Aero International in 7 days. " +
                    "Make sure you bring up cross selling for Event Weather and Aviation Property and ask for ${random.nextInt(5) + 10}% commission with every renewal."
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
            "contactCEM" -> "You client engagement manager is Aaron A Aaronson. I'll let him know"
            "carrierByClient" -> {
                val company = input.result.parameters["any"] ?: ""
                "Company $company written by American Liability"
            }
            "topClients" -> {
                val count = input.result.parameters["count"] ?: ""
                if (count != "") {
                    "You will receive top $count clients list on you email"
                } else {
                    "Your top clients are Acme Inc with ${random.nextInt(10)} products and Vect LLC with ${random.nextInt(10)} products"
                }
            }
            "topProducers" -> {
                val interval = input.result.parameters["interval"] ?: ""
                "Your top producers $interval are John Dow and Jane Row"
            }
            "whoCanWriteIndustry" -> {
                val product = input.result.parameters["product"] ?: ""
                val industry = input.result.parameters["industry"] ?: ""

                "$product for $industry could be written by Global Insurance or Liability Incorporated"
            }
            "averageCommissionWithCarrier" -> {
                val carrier = input.result.parameters["carrier"] ?: ""
                val product = input.result.parameters["product"] ?: ""

                if(product != ""){
                    "You average commission with $carrier on $product is ${random.nextInt(5) + 10}%"
                }else{
                    "You average commission with $carrier is ${random.nextInt(5) + 10}%"
                }
            }

            "highestCommissionForCarrier" -> {
                val product = input.result.parameters["product"] ?: ""
                val industry = input.result.parameters["industry"] ?: ""

                if(product != "" && industry != ""){
                    "For $product in $industry highest commission payed by " +
                            "Global Insurance ${random.nextInt(5) + 10}% " +
                            "and Liability Incorporated ${random.nextInt(5) + 10}%"
                }else if (product != ""){
                    "For $product highest commission payed by " +
                            "Global Insurance ${random.nextInt(5) + 10}% " +
                            "and Liability Incorporated ${random.nextInt(5) + 10}%"
                }else if (industry != ""){
                    "In $industry highest commission payed by " +
                            "Global Insurance ${random.nextInt(5) + 10}% " +
                            "and Liability Incorporated ${random.nextInt(5) + 10}%"
                }else{
                    "Try ask again but with product and or industry"
                }
            }
            else -> {
                "I can't understand \"${input.result.resolvedQuery}\""
            }
        }
    }
}
