package qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.json.JSONObject
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val homeScreenRepository: HomeScreenRepository
) : ViewModel() {

    private val _syncMedia: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val syncMediaResponse = _syncMedia.asStateFlow()

    private val _sendReport: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val sendReportResponse = _sendReport.asStateFlow()

    private val _updateEventNameResponse: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Empty)
    val updateEventNameResponse = _updateEventNameResponse.asStateFlow()

    fun syncTaskMedia(
        jsonObject: JSONObject?, absolutePath: String, baseUrl: String,
        subUrl: String
    ) = viewModelScope.launch {
        _syncMedia.value = ApiState.Loading
        homeScreenRepository.syncMedia(jsonObject, absolutePath, baseUrl, subUrl).catch {
            _syncMedia.value = ApiState.Failure(it)
        }.collect {
            _syncMedia.value = ApiState.Success(it)
        }
    }

    fun sendReport(
        forPM: Boolean,
        pdf: Boolean,
        event: EventData,
        forSelf: Boolean,
        baseUrl: String,
        subUrl: String
    ) = viewModelScope.launch {
        _sendReport.value = ApiState.Loading
        homeScreenRepository.sendReport(forPM, pdf, event, forSelf, baseUrl, subUrl).catch {
            _sendReport.value = ApiState.Failure(it)
        }.collect {
            _sendReport.value = ApiState.Success(it)
        }
    }

    fun sendReport(
        forPM: Boolean,
        pdf: Boolean,
        event: EventData,
        forSelf: Boolean,
        baseUrl: String,
        subUrl: String, reportId: String, reportName: String
    ) = viewModelScope.launch {
        _sendReport.value = ApiState.Loading
        homeScreenRepository.sendReport(forPM, pdf, event, forSelf, baseUrl, subUrl).catch {
            _sendReport.value = ApiState.Failure(it)
        }.collect {
            _sendReport.value = ApiState.Success(it)
        }
    }

    fun updateEventName(
        request: EventNameUpdateRequest
    ) = viewModelScope.launch {
        _updateEventNameResponse.value = ApiState.Loading
        homeScreenRepository.updateEventName(request).catch {
            _updateEventNameResponse.value = ApiState.Failure(it)
        }.collect {
            _updateEventNameResponse.value = ApiState.Success(it)
        }
    }
}