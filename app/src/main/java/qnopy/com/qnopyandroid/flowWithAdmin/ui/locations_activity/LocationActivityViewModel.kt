package qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.ui.locations_activity.models.LocationsDataRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class LocationActivityViewModel @Inject constructor(val locationsRepository: LocationsRepository):ViewModel() {
    private val _locationsState:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val locationsState:StateFlow<ApiState> = _locationsState

    fun getUserById(userId:String):String = locationsRepository.getUserNameById(userId)

    fun getDataForEventLocation(locationsDataRequest: LocationsDataRequest) = viewModelScope.launch(Dispatchers.IO){
        _locationsState.value = ApiState.Loading
        locationsRepository.getDataForEventLocation(locationsDataRequest).catch {
            _locationsState.value = ApiState.Failure(it)
        }.collect {
            _locationsState.value = ApiState.Success(it)
        }
    }
}