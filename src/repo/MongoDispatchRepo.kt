package com.reticentmonolith.repo

import com.mongodb.client.MongoDatabase
import com.reticentmonolith.models.Dispatch
import org.bson.types.ObjectId
import java.time.LocalDate

import org.litote.kmongo.*
import org.litote.kmongo.id.toId

class MongoDispatchRepo: DispatchRepoInterface {

    private val client = KMongo.createClient()
    private val database: MongoDatabase = client.getDatabase("zw")
    private val windsData = database.getCollection<Dispatch>("winds")


    override fun createDispatch(dispatch: Dispatch) {
        windsData.insertOne(dispatch)
    }

    override fun getAllDispatches(): Collection<Dispatch> {
        return windsData.find().toList()
    }

    override fun getDispatchesByDate(date: LocalDate): Collection<Dispatch> {
        return windsData.find(Dispatch::date eq date).toList()
    }

    override fun getDispatchesByDateRange(start: LocalDate, end: LocalDate): Collection<Dispatch> {
        return windsData.find(Dispatch::date gte(start), Dispatch::date lte(end)).toList()
    }

    override fun getDispatchById(id: Id<Dispatch>): Dispatch? {
        return windsData.findOneById(id)
    }

    override fun updateDispatchById(id: Id<Dispatch>, update: Dispatch): Dispatch? {
        val oldDispatch = getDispatchById(id)
        if (oldDispatch != null) {
            update.time = oldDispatch.time
            update.date = oldDispatch.date
            update._id = oldDispatch._id
            windsData.updateOneById(id, update)
            return update
        }
        return null
    }

    override fun updateLastDispatch(update: Dispatch): Boolean {
        val lastDispatch = getLastDispatch() ?: return false
        update.time = lastDispatch.time
        update.date = lastDispatch.date
        update._id = lastDispatch._id
        windsData.updateOneById(lastDispatch._id, update)
        return true
    }

    override fun deleteDispatchById(id: Id<Dispatch>) {
        windsData.deleteOne(Dispatch::_id eq id)
    }
    
}
