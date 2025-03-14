package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class TelegramBotService(
    val apiKey: String,
    private val client: HttpClient = HttpClient.newBuilder().build(),
) {
    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$apiKey/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Long, message: String): String {
        var textToSend = message
        var partOfMessage: String
        var response: String
        val responses = mutableListOf<String>()

        if (textToSend.isEmpty()) return "Отправка невозможна.Недопустимая длина сообщения."
        while (textToSend.isNotEmpty()) {
            partOfMessage = textToSend.take(MAX_LENGTH_OF_MESSAGE)
            response = sendMessagePart(chatId = chatId, message = partOfMessage)
            responses.add(response)
            textToSend = textToSend.drop(MAX_LENGTH_OF_MESSAGE)
        }
        return responses.joinToString("\n")
    }

    private fun sendMessagePart(message: String, chatId: Long): String {
        val encodedText = URLEncoder.encode(message, "UTF-8")
        val urlSendMessage =
            "$API_URL$apiKey/sendMessage?chat_id=$chatId&text=$encodedText"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}

