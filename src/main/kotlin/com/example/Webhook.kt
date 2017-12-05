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
 *  // TODO put authentication
 *  // TODO extract methods
 *  // TODO put context
 *  // TODO put fallback
 */
@WebServlet("/webhook")
class Webhook : AIWebhookServlet() {
    private val random = Random()

    override fun doWebhook(input: AIWebhookRequest, output: Fulfillment) {
        System.out.println(Gson().toJson(input))
        val action = input.result.action
        fun param(param: String) = input.result.getStringParameter(param)

        output.speech = when(action) {
            "averageCommission" -> {
                val interval = param("interval")

                when {
                    exists(interval) -> "You average commission $interval is ${randomInt(5, 10)}%"
                    else -> "You average commission year to date is ${randomInt(5, 10)}%"
                }
            }
            "averageCommissionInTheMarket" -> {
                val product = param("product")
                val industry = param("industry")

                when {
                    exists(product, industry) -> "For $product in $industry average commission is ${randomInt(5, 10)}%"
                    exists(product) -> "For $product average commission is ${randomInt(5, 10)}%"
                    exists(industry) -> "In $industry average commission is ${randomInt(5, 10)}%"
                    else -> "Try ask again but with product and or industry"
                }
            }
            "averageCommissionWithCarrier" -> {
                val carrier = param("carrier")
                val product = param("product")

                when {
                    exists(product) -> "You average commission with $carrier on $product is ${randomInt(5, 10)}%"
                    else -> "You average commission with $carrier is ${randomInt(5, 10)}%"
                }
            }
            "belowAverageCommission" -> {
                val product = param("product")
                val interval = param("interval")

                when {
                    exists(product, interval) -> "${randomInt(20, 50)}% of your accounts are below average commission $interval for $product"
                    exists(product) -> "${randomInt(20, 50)}% of your accounts are below average commission year to date for $product"
                    exists(interval) -> "${randomInt(20, 50)}% of your accounts are below average commission $interval"
                    else -> "Please specify product and/or interval"
                }
            }
            "carrierProfile" -> {
                val carrier = param("carrier")

                "I'll send you profile for $carrier on email"
            }
            "clientByNumber" -> {
                val clientNumber = param("clientNumber")

                "Client $clientNumber is ${companies.sample()} and have ${randomInt(10)} products"
            }
            "contactCEM" -> {
                val any = param("any")
                when {
                    exists(any) -> "You client engagement manager is ${names.sample()}. I'll let know that you need help with $any"
                    else -> "You client engagement manager is ${names.sample()}. I'll let know that you need help"
                }
            }
            "carrierForProduct" -> {
                val product = param("product")
                "For $product the best carriers are ${carriers.sample()} and ${carriers.sample()}"
            }
            "carrierByClient" -> {
                val company = param("any")
                "Company $company written by ${carriers.sample()}"
            }
            "crossSell" -> {
                val segment1 = param("insuranceSegment1")
                val segment2 = param("insuranceSegment2")

                "Between $segment1 and $segment2 you have $${randomInt(10)} millions cross sell"
            }
            "highestCommissionForCarrier" -> {
                val product = param("product")
                val industry = param("industry")

                when {
                    exists(product, industry) -> "For $product in $industry highest commission payed by " +
                            "${carriers.sample()} ${randomInt(5, 10)}% " +
                            "and ${carriers.sample()} ${randomInt(5, 10)}%"
                    exists(product) -> "For $product highest commission payed by " +
                            "${carriers.sample()} ${randomInt(5, 10)}% " +
                            "and ${carriers.sample()} ${randomInt(5, 10)}%"
                    exists(industry) -> "In $industry highest commission payed by " +
                            "${carriers.sample()} ${randomInt(5, 10)}% " +
                            "and ${carriers.sample()} ${randomInt(5, 10)}%"
                    else -> "Try ask again but with product and or industry"
                }
            }
            "insuranceSegmentHighlights" -> {
                val segment = param("insuranceSegment")

                val clients = randomInt(100)
                val premium = randomInt(1000)

                "In $segment you have $clients clients with $$premium million premium"
            }
            "productHighlights" -> {
                val product = param("product")
                val industry = param("industry")

                val clients = randomInt(100)
                val premium = randomInt(1000)

                when {
                    exists(product, industry) -> "For $product in $industry you have $clients clients with $$premium million premium"
                    exists(product) -> "For $product you have $clients clients with $$premium million premium"
                    exists(industry) -> "In $industry you have $clients clients with $$premium million premium"
                    else -> "Please specify product and/or industry"
                }
            }
            "renewals" -> {
                val number = param("number")

                when {
                    exists(number) -> "In next $number days "
                    else -> ""
                } +
                        "You have ${products.sample()} for ${companies.sample()} in ${randomInt(7)} days and " +
                        "${products.sample()} for ${companies.sample()} in ${randomInt(7)} days. " +
                        "Make sure you bring up cross selling for ${products.sample()} and ${products.sample()} and also " +
                        "ask for ${random.nextInt(5) + 10}% commission with every renewal."
            }
            "revenue" -> {
                val interval = param("interval")

                when {
                    exists(interval) -> "Your revenue $interval is $${randomInt(10)} millions"
                    else -> "Your revenue Year to date is $${randomInt(10)} millions"
                }
            }
            "topCarriers" -> {
                "Your top carriers by Premium In Force are " +
                        "${carriers.sample()} with $${randomInt(10)} millions " +
                        "and ${carriers.sample()} with $${randomInt(10)} millions"
            }
            "topClients" -> {
                val count = param("count")
                when {
                    exists(count) -> "You will receive top $count clients list on you email"
                    else -> "Your top clients are ${companies.sample()} with ${random.nextInt(10)} products and " +
                            "${companies.sample()} with ${random.nextInt(10)} products"
                }
            }
            "topRoles" -> {
                val interval = param("interval")
                val role = param("role")
                when (role) {
                    "account owners by revenue" -> {
                        when {
                            exists(interval) -> "Your top account owners by revenue $interval are " +
                                    "${names.sample()} with $${randomInt(10)} millions " +
                                    "and ${names.sample()} with $${randomInt(10)} millions"
                            else -> "Your top account owners by revenue year to date are " +
                                    "${names.sample()} with $${randomInt(10)} millions " +
                                    "and ${names.sample()} with $${randomInt(10)} millions"
                        }
                    }
                    "revenue producers" -> {
                        when {
                            exists(interval) -> "Your top revenue producers $interval are " +
                                    "${names.sample()} with $${randomInt(10)} millions " +
                                    "and ${names.sample()} with $${randomInt(10)} millions"
                            else -> "Your top revenue producers year to date are " +
                                    "${names.sample()} with $${randomInt(10)} millions " +
                                    "and ${names.sample()} with $${randomInt(10)} millions"
                        }
                    }
                    else -> "I don't know about $role yet"
                }
            }
            "whoCanWriteClient" -> {
                val product = param("product")
                val client = param("client")

                "$product for $client could be written by ${carriers.sample()} or ${carriers.sample()}"
            }
            "whoCanWriteIndustry" -> {
                val product = param("product")
                val industry = param("industry")

                "$product in $industry could be written by ${carriers.sample()} or ${carriers.sample()}"
            }
            else -> {
                "I can't understand \"${input.result.resolvedQuery}\""
            }
        }
    }

    private fun exists(vararg params: String?): Boolean {
        return !params.any { it.isNullOrEmpty() }
    }

    private fun randomInt(to: Int): Int = randomInt(1, to)
    private fun randomInt(from: Int, to: Int): Int = random.nextInt(to - from) + from

    private fun <T> MutableList<T>.sample(): T = this[randomInt(this.size)]
    private fun <T> MutableList<T>.sample(count: Int): List<T> {
        Collections.shuffle(this)
        return this.take(count)
    }

    private val products = mutableListOf(
            "Group Medical", "Aviation Liability", "Aviation Property", "Construction Miscellaneous", "Liability General",
            "Event Weather", "Crime", "Intellectual Property", "Marine Cargo", "Marine Liability",
            "Property Risk", "Reinsurance")
    // http://random-name-generator.info/random/
    private val names = mutableListOf(
            "Phillip Moore", "Virginia Allen", "Martha Johnson", "Steven Hill", "Christopher Price",
            "Sean Thomas", "Robert Martinez", "Mark Gonzalez", "Ruby Hughes", "Judy Evans")
    // http://www.mockaroo.com
    private val companies = mutableListOf(
            "Plain Spoon LLP", "Sunny Penguin LLP", "Glass LLP", "ICON plc", "Qwest Corporation",
            "WEX Inc.", "Trinseo S.A.", "Lawson Products, Inc.", "Data I/O Corporation",
            "Risus Inc.", "Landstar System, Inc.", "Dorian LPG", "Denbury Resources Inc.", "Morbi LLC",
            "Clearfield, Inc.", "Hudson Global, Inc.", "Cras Limited", "Novavax, Inc.", "Delphi Automotive plc", "Cognex Corporation")
    // https://en.wikipedia.org/wiki/List_of_United_States_insurance_companies
    private val carriers = mutableListOf(
            "Allstate", "Applied Underwriters", "Blue Advantage", "Chubb Corp", "Esurance",
            "FM Global", "General Re", "Hanover Insurance", "Insurance Panda", "Knights of Columbus",
            "Liberty Mutual", "Mercury Insurance Group", "National Life", "Omega", "Pacific Life",
            "Primerica", "Pure Insurance", "Safeco", "Symetra", "XL Catlin"
    )
}
