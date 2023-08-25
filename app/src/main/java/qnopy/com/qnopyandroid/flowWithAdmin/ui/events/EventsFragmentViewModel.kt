package qnopy.com.qnopyandroid.flowWithAdmin.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class EventsFragmentViewModel @Inject constructor(private val eventsRepository: EventsRepository) :
    ViewModel() {

    private val _getAllEventsFromDb: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val getAllEventsFromDb: StateFlow<ApiState> = _getAllEventsFromDb

    fun getAllEventsMap(): HashMap<String, ArrayList<EventData>> =
        eventsRepository.getAllEventsMap()

/*    fun getAllEvents(siteId: String): Triple<HashMap<String, ArrayList<dashboard_data_card>>,
            ArrayList<dashboard_data_card>, HashMap<String, Int>> =
        eventsRepository.getAllEvents(siteId)*/

    fun getAllEvents(siteId: String) = viewModelScope.launch {
        _getAllEventsFromDb.value = ApiState.Loading
        eventsRepository.getAllEvents(siteId).catch {
            _getAllEventsFromDb.value = ApiState.Failure(it)
        }.collect {
            _getAllEventsFromDb.value = ApiState.Success(it)
        }
    }

    fun getUserById(userId: String): String = eventsRepository.getUserNameById(userId)
}