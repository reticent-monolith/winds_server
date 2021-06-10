package com.reticentmonolith

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.reticentmonolith.repo.MongoDispatchRepo
import io.ktor.jackson.*


fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val repo = MongoDispatchRepo()

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(JsonMapper.defaultMapper))
    }

    routing {
        get("/") {
            call.response.status(HttpStatusCode.OK)
            val dispatches = repo.getAllDispatches()
            call.respond(dispatches)

        }
    }
}

