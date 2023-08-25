package qnopy.com.qnopyandroid.flowWithAdmin.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val settingRepository: SettingRepository) :
    ViewModel() {

    private val _openIdLogoutResponse: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val openIdLogoutResponse: StateFlow<ApiState> = _openIdLogoutResponse

    fun getDataForCSV(date: String) = settingRepository.getDataForCSV(date)
    fun isDataAvailableToSync() = settingRepository.isDataAvailableToSync
    fun getUserRole(userName: String): Int = settingRepository.getUserRole(userName)

    fun logoutOpenIdAuth(logoutUrl: String) = viewModelScope.launch {
        _openIdLogoutResponse.value = ApiState.Loading
        settingRepository.logoutOpenIdAuth(logoutUrl).catch {
            _openIdLogoutResponse.value = ApiState.Failure(it)
        }.collect {
            _openIdLogoutResponse.value = ApiState.Success(it)
        }
    }
}