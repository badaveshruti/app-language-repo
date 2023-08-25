package qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.db.LocationDataSource
import javax.inject.Inject
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity.models.LocationsDataRequest


class LocationsRepository @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val userDataSource: UserDataSource
) {

    fun getUserNameById(userid: String) = userDataSource.getUserNameFromID(userid)

    fun getDataForEventLocation(locationsDataRequest: LocationsDataRequest): Flow<HashMap<String, ArrayList<Location>>> =
        flow {
            emit(
                locationDataSource.getAllDataLocFormDefaultOrNon(
                    locationsDataRequest.siteID, locationsDataRequest.rollAppID
                )
            )
        }.flowOn(Dispatchers.IO)
}