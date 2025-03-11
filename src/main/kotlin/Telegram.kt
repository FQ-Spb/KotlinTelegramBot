package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId: Int? = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        val updateIdRegex = "\"update_id\":(.+?),".toRegex()
        val matchIdRegex = updateIdRegex.find(updates)
        val groupsId = matchIdRegex?.groups
        updateId = (groupsId?.get(1)?.value?.toInt() ?: 0) + 1
        println(updateId)

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

    }
}

fun getUpdates(token: String, updateId: Int?): String {
    val client: HttpClient = HttpClient.newBuilder().build()
    val urlGetUpdates = "https://api.telegram.org/bot$token/getUpdates?offset=$updateId"
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}