package qnopy.com.qnopyandroid.flowWithAdmin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    ViewModel() {

    fun getRecentEventsCard(): ArrayList<EventData> = homeRepository.getRecentEventsCard()
    fun getSiteListForUser(userId: String): List<Site> = homeRepository.getSiteListForUser(userId)

    private val _favProjectState: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val favStateFlow: StateFlow<ApiState> = _favProjectState
    var siteID: Int = 0
    var favStatus: Boolean = false

    fun setFavouriteProject(favouriteProjectRequest: FavouriteProjectRequest, projectPos: Int) =
        viewModelScope.launch {
            _favProjectState.value = ApiState.Loading
            homeRepository.setFavouriteProject(favouriteProjectRequest).catch {
                _favProjectState.value = ApiState.Failure(it)
            }.collect {
                _favProjectState.value = ApiState.Success(it)
            }
        }

    fun updateProjectFavourite(siteId: String, userId: String, favStatus: Boolean) =
        homeRepository.updateFavStatus(siteId, userId, favStatus)
}
