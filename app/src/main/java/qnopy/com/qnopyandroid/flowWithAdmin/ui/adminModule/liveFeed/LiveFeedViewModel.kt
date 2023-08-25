package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class LiveFeedViewModel @Inject constructor(private val liveFeedRepository: LiveFeedRepository) :
    ViewModel() {

    private val _fetchLiveFeedSF = MutableSharedFlow<ApiState>()
    val fetchLiveFeedSF = _fetchLiveFeedSF.asSharedFlow()

    fun getLiveFeed(siteId: String, lastSyncDate: String) = viewModelScope.launch {
        _fetchLiveFeedSF.emit(ApiState.Loading)
        liveFeedRepository.getLiveFeed(siteId, lastSyncDate).catch {
            _fetchLiveFeedSF.emit(ApiState.Failure(it))
        }.collect {
            _fetchLiveFeedSF.emit(ApiState.Success(it))
        }
    }
}