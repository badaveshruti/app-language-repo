package qnopy.com.qnopyandroid.flowWithAdmin.ui.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.db.EventDataSource
import qnopy.com.qnopyandroid.db.EventDbSource
import qnopy.com.qnopyandroid.db.UserDataSource
import javax.inject.Inject

class EventsRepository @Inject constructor(
    private val eventDbSource: EventDbSource,
    private val eventDataSource: EventDataSource,
    private val userDataSource: UserDataSource
) {

    fun getAllEventsMap(): HashMap<String, ArrayList<EventData>> =
        eventDataSource.getAllEvents(null)

    fun getAllEvents(siteId: String): Flow<Triple<HashMap<String, ArrayList<EventData>>,
            ArrayList<EventData>, HashMap<String, Int>>> = flow {
        emit(getEvents(siteId))
    }.flowOn(Dispatchers.IO)

    private suspend fun getEvents(siteId: String): Triple<HashMap<String, ArrayList<EventData>>,
            ArrayList<EventData>, HashMap<String, Int>> {
        return eventDbSource.getAllEvents(siteId)
    }

    fun getUserNameById(userid: String): String = userDataSource.getFullName(userid)
}