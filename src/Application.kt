package com.reticentmonolith

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.reticentmonolith.models.Dispatch
import com.reticentmonolith.models.Rider
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.reticentmonolith.repo.MongoDispatchRepo
import io.ktor.jackson.*
import org.litote.kmongo.id.jackson.IdJacksonModule


fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val repo = MongoDispatchRepo()

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(JsonMapper.defaultMapper))
    }

    repo.createDispatch(
        Dispatch(
            bt_radio = "kdjlfg",
            wind_degrees = 3,
            wind_speed = 3.7,
            winds_instructor = "kashd"
        ).apply {
            this.riders[2] = Rider(67)
        }
    )

    routing {
        get("/") {
            call.response.status(HttpStatusCode.OK)

            val dispatches = repo.getAllDispatches()

            call.respond(dispatches)

        }
    }
}

