package com.reticentmonolith

import com.reticentmonolith.models.Dispatch
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.reticentmonolith.repo.MongoDispatchRepo
import io.ktor.jackson.*
import io.ktor.request.*
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import java.time.LocalDate
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.lang.IllegalArgumentException


fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val repo = MongoDispatchRepo()

    install(ContentNegotiation) {
        register(
            ContentType.Application.Json,
            JacksonConverter(JsonMapper.defaultMapper)
        )
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true
        anyHost()
    }


    routing {
        get("/") {
            call.response.status(HttpStatusCode.OK)
            val dispatches = repo.getAllDispatches()
            call.respond(dispatches)
        }

        post("/") {
            call.response.status(HttpStatusCode.OK)
            println("#### Getting dispatch...####")
            val dispatch = call.receive<Dispatch>()
            println("#### Got $dispatch ####")
            repo.createDispatch(dispatch)
            call.respond(dispatch._id)
        }

        get("/getbydate/{date}") {
            call.response.status(HttpStatusCode.OK)

            val reqDate = call.parameters["date"] ?: return@get call.respondText(
                "Malformed date", status=HttpStatusCode.BadRequest
            )
            val listDate: List<Int> = reqDate.split("-").map {it.toInt()}
            val date = LocalDate.of(listDate[0], listDate[1], listDate[2])
            val dispatches = repo.getDispatchesByDate(date)
            call.respond(dispatches)
        }

        post("/update/{id}") {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Malformed id", status=HttpStatusCode.BadRequest
            )
            val objId: ObjectId
            try {
                objId = ObjectId(id)
            } catch (e: IllegalArgumentException) {
                return@post call.respondText(
                    "Malformed id", status=HttpStatusCode.BadRequest
                )
            }
            repo.updateDispatchById(objId.toId(), call.receive()) ?: return@post call.respondText(
                "Cannot find that dispatch...", status= HttpStatusCode.NotFound
            )
            call.response.status(HttpStatusCode.OK)
            call.respond("$id updated")
        }

        get("/get/{id}") {

            val id = call.parameters["id"] ?: return@get call.respondText(
                "Malformed id", status=HttpStatusCode.BadRequest
            )

            val objId: ObjectId
            try {
                objId = ObjectId(id)
            } catch (e: IllegalArgumentException) {
                return@get call.respondText(
                    "Malformed id", status=HttpStatusCode.BadRequest
                )
            }

            val dispatch = repo.getDispatchById(objId.toId()) ?: return@get call.respondText(
                "Cannot find that dispatch...", status= HttpStatusCode.NotFound
            )

            call.response.status(HttpStatusCode.OK)
            call.respond(dispatch)
        }

        post("/addspeeds/{id}") {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Malformed id", status=HttpStatusCode.BadRequest
            )
            val objId: ObjectId
            try {
                objId = ObjectId(id)
            } catch (e: IllegalArgumentException) {
                return@post call.respondText(
                    "Malformed id", status=HttpStatusCode.BadRequest
                )
            }
            val dispatch = repo.getDispatchById(objId.toId()) ?: return@post call.respondText(
                "Cannot find that dispatch...", status= HttpStatusCode.NotFound
            )
            call.response.status(HttpStatusCode.OK)
            val speeds = Json.decodeFromString<HashMap<Int, Int?>>(call.receive())
            println(speeds)
            repo.addSpeedsToDispatch(dispatch, speeds[4], speeds[3], speeds[2], speeds[1])
            call.respond("Speeds added")
        }
    }
}

