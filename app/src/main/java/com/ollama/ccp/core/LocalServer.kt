package com.ollama.ccp.core

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.*

@Serializable
data class ChatRequest(val model: String, val messages: List<ChatMessage>, val stream: Boolean = false)

@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
data class ChatResponse(val model: String, val message: ChatMessage, val done: Boolean)

class LocalServer(private val llmEngine: LLMEngine) {
    private var server: NettyApplicationEngine? = null

    fun start(port: Int = 11434) {
        server = embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            routing {
                get("/") {
                    call.respondText("OllamaCCP Server Running")
                }
                post("/api/chat") {
                    val request = call.receive<ChatRequest>()
                    if (request.stream) {
                        call.respondTextWriter(contentType = ContentType.Application.Json) {
                            val prompt = request.messages.lastOrNull()?.content ?: ""
                            llmEngine.chatStream(prompt, object : TokenCallback {
                                override fun onToken(token: String) {
                                    val json = Json.encodeToString(ChatResponse.serializer(), ChatResponse(request.model, ChatMessage("assistant", token), false))
                                    write("$json\n")
                                    flush()
                                }
                                override fun onComplete() {
                                    val json = Json.encodeToString(ChatResponse.serializer(), ChatResponse(request.model, ChatMessage("assistant", ""), true))
                                    write("$json\n")
                                    flush()
                                }
                            })
                        }
                    } else {
                        // Blocking/Non-streaming response
                        call.respond(ChatResponse(request.model, ChatMessage("assistant", "Local generation placeholder"), true))
                    }
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}
