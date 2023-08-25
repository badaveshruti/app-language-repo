package qnopy.com.qnopyandroid.flowWithAdmin.ui.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.db.EventDataSource
import qnopy.com.qnopyandroid.db.SiteDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectResponse
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val eventsDataSource: EventDataSource,
    private val siteDataSource: SiteDataSource,
    private val apiServiceImpl: ApiServiceImpl
) {

    fun getRecentEventsCard(): ArrayList<EventData> = eventsDataSource.recent10Events
    fun getSiteListForUser(userId: String): List<Site> = siteDataSource.getAllSitesForUser(userId)

    fun setFavouriteProject(favouriteProjectRequest: FavouriteProjectRequest): Flow<FavouriteProjectResponse> =
        flow {
            emit(apiServiceImpl.setFavouriteProject(favouriteProjectRequest))
        }.flowOn(Dispatchers.IO)

    fun updateFavStatus(siteId: String, userId: String, favStatus: Boolean) {
        siteDataSource.updateFavStatus(siteId, userId, favStatus)
    }

}