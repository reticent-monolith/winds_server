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
        method(HttpMethod.Post)
        method(HttpMethod.Options)

        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.Accept)
        header(HttpHeaders.ContentLanguage)
        header(HttpHeaders.ContentType)

        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true

        // host("reticent-monolith.com", subDomains = listOf("winds"), schemes=listOf("https"))
        anyHost()
    }

    routing {
        // Get dispatches by specified date
        get("/bydate/{date}") {
            call.response.status(HttpStatusCode.OK)
            val date = call.parameters["date"]?.replace('-', '/')
            println(date)
            call.respond(repo.getDispatchesByDate(date!!))
        }

        // Get dispatches in specified date range (not used in front end)
        get("/bydaterange") {
            call.response.status(HttpStatusCode.OK)
            val start = call.request.queryParameters["start"]
            println(start)
            val end = call.request.queryParameters["end"]
            println(end)
            val dispatches = repo.getDispatchesByDateRange(LocalDate.parse(start), LocalDate.parse(end))
            call.respond(dispatches)
        }

        // Get dispatch by ID
        get("/byid/{id}") {
            call.response.status(HttpStatusCode.OK)
            val idString = call.parameters["id"]
            val id = ObjectId(idString).toId<Dispatch>()
            val dispatch = repo.getDispatchById(id)
            if (dispatch != null) {
                call.respond(dispatch)
            }
        }

        // Add a dispatch
        post("/add") {
            call.response.status(HttpStatusCode.OK)
            val dispatch = call.receive<Dispatch>()
            repo.createDispatch(dispatch)
            call.respond(dispatch._id)
        }
        options("/add") {
            call.response.status(HttpStatusCode.OK)
            call.response.headers.append(HttpHeaders.AccessControlAllowHeaders, "content-type")
            call.respond("all good!")
        }

        // Delete a dispatch
        post("/delete") {
            call.response.status(HttpStatusCode.OK)
            val id = call.receive<String>()
            val dispatch = ObjectId(id).toId<Dispatch>()
            repo.deleteDispatchById(dispatch)
            call.respond(id)
        }

        // Update a dispatch
        post("/update") {
            call.response.status(HttpStatusCode.OK)
            val dispatch = call.receive<Dispatch>()
            repo.updateDispatchById(dispatch._id, dispatch)
            call.respond(dispatch._id)
        }
        options("/update") {
            call.response.status(HttpStatusCode.OK)
            call.response.headers.append(HttpHeaders.AccessControlAllowHeaders, "content-type")
            call.respond("all good!")
        }
    }
}

