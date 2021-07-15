package com.reticentmonolith.repo

import com.mongodb.client.MongoDatabase
import com.reticentmonolith.models.Dispatch
import org.litote.kmongo.*
import java.time.LocalDate
import java.net.UnknownHostException

class MongoDispatchRepo: DispatchRepoInterface {

    private val client = try {
        KMongo.createClient("mongodb://db:27017")
    } catch (e: Exception) {
        println("Running on dev db!")
        KMongo.createClient("mongodb://localhost:27017")
    }
    private val database: MongoDatabase = client.getDatabase("zw")
    private val windsData = database.getCollection<Dispatch>("winds")


    override fun createDispatch(dispatch: Dispatch) {
        windsData.insertOne(dispatch)
    }

    override fun getAllDispatches(): Collection<Dispatch> {
        return windsData.find().toList()
    }

    override fun getDispatchesByDate(requestDate: String): Collection<Dispatch> {
        val dispatches = this.getAllDispatches()
        val filtered = dispatches.filter {
            requestDate == it.date
        }
        return filtered
    }

    override fun getDispatchesByDateRange(start: LocalDate, end: LocalDate): Collection<Dispatch> {
        val dispatches = this.getAllDispatches()
        val filtered = dispatches.filter {
            val date = LocalDate.parse(it.date)
            (date.isEqual(start) || date.isAfter(start)) && (date.isEqual(end) || date.isBefore(end))
        }
        return filtered
    }

    override fun getDispatchById(id: Id<Dispatch>): Dispatch? {
        return windsData.findOneById(id)
    }

    override fun updateDispatchById(id: Id<Dispatch>, update: Dispatch): Dispatch? {
        val oldDispatch = getDispatchById(id)
        if (oldDispatch != null) {
            update.date = oldDispatch.date
            update.time = oldDispatch.time
            update._id = oldDispatch._id
            windsData.updateOneById(id, update)
            return update
        }
        return null
    }

    override fun deleteDispatchById(id: Id<Dispatch>) {
        windsData.deleteOne(Dispatch::_id eq id)
    }

    fun clearCollection() {
        windsData.deleteMany("{}")
    }
    
}
