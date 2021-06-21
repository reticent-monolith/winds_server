package com.reticentmonolith.repo

import com.reticentmonolith.models.Dispatch
import java.time.LocalDate
import org.litote.kmongo.Id

interface DispatchRepoInterface {

    fun createDispatch(dispatch: Dispatch)
    fun getAllDispatches(): Collection<Dispatch>
    fun getDispatchesByDate(date: LocalDate): Collection<Dispatch>
    fun getDispatchesByDateRange(start: LocalDate, end: LocalDate): Collection<Dispatch>
    fun getDispatchById(id: Id<Dispatch>): Dispatch?
    fun updateDispatchById(id: Id<Dispatch>, update: Dispatch): Dispatch?
    fun deleteDispatchById(id: Id<Dispatch>)
}