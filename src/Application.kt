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
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)

        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.Accept)
        header(HttpHeaders.ContentLanguage)
        header(HttpHeaders.ContentType)

        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true

        host("winds.dev", subDomains = listOf("front","back"), schemes=listOf("https"))

    }

    routing {
        get("/today") {
            call.response.status(HttpStatusCode.OK)
            call.respond(repo.getDispatchesByDate(LocalDate.now().toString()))
        }
        get("/bydate/{date}") {
            call.response.status(HttpStatusCode.OK)
            val date = call.parameters["date"]
            call.respond(repo.getDispatchesByDate(LocalDate.parse(date).toString()))
        }
        get("/bydaterange") {
            call.response.status(HttpStatusCode.OK)
            val start = call.request.queryParameters["start"]
            println(start)
            val end = call.request.queryParameters["end"]
            println(end)
            val dispatches = repo.getDispatchesByDateRange(LocalDate.parse(start), LocalDate.parse(end))
            call.respond(dispatches)
        }
        get("/byid/{id}") {
            call.response.status(HttpStatusCode.OK)
            val idString = call.parameters["id"]
            val id = ObjectId(idString).toId<Dispatch>()
            val dispatch = repo.getDispatchById(id)
            if (dispatch != null) {
                call.respond(dispatch)
            }
        }

        post("/add") {
            call.response.status(HttpStatusCode.OK)
            val dispatch = call.receive<Dispatch>()
            repo.createDispatch(dispatch)
            call.respond(dispatch)
        }
        options("/add") {
            call.response.status(HttpStatusCode.OK)
            call.response.headers.append(HttpHeaders.AccessControlAllowHeaders, "content-type")
            call.respond("all good!")
        }
        post("/delete") {
            call.response.status(HttpStatusCode.OK)
            val id = call.receive<String>()
            val dispatch = ObjectId(id).toId<Dispatch>()
            repo.deleteDispatchById(dispatch)
            call.respond("$id deleted!")
        }
        post("/update") {
            call.response.status(HttpStatusCode.OK)
            val dispatch = call.receive<Dispatch>()
            repo.updateDispatchById(dispatch._id, dispatch)
            call.respond(dispatch)
        }
        options("/update") {
            call.response.status(HttpStatusCode.OK)
            call.response.headers.append(HttpHeaders.AccessControlAllowHeaders, "content-type")
            call.respond("all good!")
        }

        delete("/purge") {
            call.response.status(HttpStatusCode.OK)
            repo.clearCollection()
            call.respond("All gone!")
        }
    }
}

