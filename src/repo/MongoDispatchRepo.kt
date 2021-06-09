package com.reticentmonolith.repo

import com.mongodb.client.MongoDatabase
import com.reticentmonolith.models.Dispatch
import java.time.LocalDate

import org.litote.kmongo.*

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

    override fun updateDispatchById(id: Id<Dispatch>, update: Dispatch): Boolean {
        val oldDispatch = getDispatchById(id)
        if (oldDispatch != null) {
            update.time = oldDispatch.time
            update.date = oldDispatch.date
            update._id = oldDispatch._id
            windsData.updateOneById(id, update)
            return true
        }
        return false
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

    override fun getLastDispatch(): Dispatch? {
        val todaysDispatches = getDispatchesByDate(LocalDate.now())
        if (todaysDispatches.isEmpty()) return null
        return todaysDispatches.last()
    }

    override fun addSpeedsToLastDispatch(line4: Int?, line3: Int?, line2: Int?, line1: Int?) {
        val lastDispatch = getLastDispatch() ?: return
        lastDispatch.riders.get(4)?.speed = line4
        lastDispatch.riders.get(3)?.speed = line3
        lastDispatch.riders.get(2)?.speed = line2
        lastDispatch.riders.get(1)?.speed = line1
        updateLastDispatch(lastDispatch)

    }
}