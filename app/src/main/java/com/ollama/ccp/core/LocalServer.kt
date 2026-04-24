package com.ollama.ccp.core

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChatRequest(val model: String, val messages: List<ChatMessage>)

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
                    // Simplified: just echos back for now
                    call.respond(ChatResponse("model", ChatMessage("assistant", "Local API Response"), true))
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}
