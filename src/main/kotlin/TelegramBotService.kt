package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class TelegramBotService {
    fun getUpdates(token: String, updateId: Int?): String {
        val client: HttpClient = HttpClient.newBuilder().build()
        val urlGetUpdates = "https://api.telegram.org/bot$token/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(token: String, chatId: Int?, message: String): String {
        var text = message
        val client = HttpClient.newBuilder().build()
        val responses = mutableListOf<String>()

        if (text.isEmpty()) return "Отправка невозможна.Недопустимая длина сообщения."
        while (text.isNotEmpty()) {
            val firstPartOfMessage = URLEncoder.encode(text.take(MAX_LENGTH_OF_MESSAGE), "UTF-8")
            val urlSendMessage =
                "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$firstPartOfMessage"
            val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            responses.add(response.body())
            text = text.drop(MAX_LENGTH_OF_MESSAGE)
        }

        return responses.joinToString("\n")
    }
}