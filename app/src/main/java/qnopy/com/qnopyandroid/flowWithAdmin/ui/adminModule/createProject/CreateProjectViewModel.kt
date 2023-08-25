package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val createProjectRepository: CreateProjectRepository
) : ViewModel() {

    private val _createProjectFlow = MutableSharedFlow<ApiState>()
    val createProjectResFlow = _createProjectFlow.asSharedFlow()

    fun createProject(request: CreateProjectRequest) = viewModelScope.launch(Dispatchers.IO) {
        _createProjectFlow.emit(ApiState.Loading)
        createProjectRepository.createProject(request).catch {
            _createProjectFlow.emit(ApiState.Failure(it))
        }.collect {
            _createProjectFlow.emit(ApiState.Success(it))
        }
    }
}