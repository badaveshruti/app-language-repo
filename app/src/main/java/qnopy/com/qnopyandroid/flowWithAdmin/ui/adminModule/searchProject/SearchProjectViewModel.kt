package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.Project
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class SearchProjectViewModel @Inject constructor(private val searchProjectRepository: SearchProjectRepository) :
    ViewModel() {

    private val _projectState: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val projectState: StateFlow<ApiState> = _projectState

    private val _assignProjectSF: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val assignProjectSF: StateFlow<ApiState> = _assignProjectSF

    var vProject: Project = Project()

    fun fetchProjectList(projectListRequest: ProjectListRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _projectState.value = ApiState.Loading
            searchProjectRepository.fetchProjectList(projectListRequest).catch {
                _projectState.value = ApiState.Failure(it)
            }.collect {
                _projectState.value = ApiState.Success(it)
            }
        }

    fun assignProject(siteId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _assignProjectSF.value = ApiState.Loading
            searchProjectRepository.assignProject(siteId).catch {
                _assignProjectSF.value = ApiState.Failure(it)
            }.collect {
                _assignProjectSF.value = ApiState.Success(it)
            }
        }
}