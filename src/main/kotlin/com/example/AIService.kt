package com.example

import ai.api.AIServiceException
import ai.api.web.AIServiceServlet
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebInitParam
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * curl "http://localhost:8080/ai?query=hello"
 */
@WebServlet(
        urlPatterns = arrayOf("/ai"),
        initParams = arrayOf(WebInitParam(
                name = AIServiceServlet.PARAM_API_AI_KEY,
                // FIXME
                value = "1bea0a262c924f43bf87a88e4a69cd94")))
class MyServiceServlet : AIServiceServlet() {
    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val aiResponse = request(request.getParameter("query"), request.getSession())
            response.contentType = "text/plain"
            response.writer.append(aiResponse.result.fulfillment.speech)
        } catch (e: AIServiceException) {
            e.printStackTrace()
        }

    }
}
