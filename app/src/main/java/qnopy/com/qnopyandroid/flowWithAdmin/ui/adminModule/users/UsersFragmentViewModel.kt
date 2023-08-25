package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.db.FieldDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.requestmodel.SUser
import javax.inject.Inject

@HiltViewModel
class UsersFragmentViewModel @Inject constructor(private val usersRepository: UsersRepository) :
    ViewModel() {

    private val _assignUserSharedFlow = MutableStateFlow<ApiState>(ApiState.Empty)
    val assignUserResFlow = _assignUserSharedFlow.asStateFlow()

    private val _deAssignUserSharedFlow = MutableStateFlow<ApiState>(ApiState.Empty)
    val deAssignUserResFlow = _deAssignUserSharedFlow.asStateFlow()

    fun getUsersListForSite(userId: String, companyId: Int, siteId: Int): ArrayList<SUser> =
        usersRepository.getUsersListForSite(userId, companyId, siteId)

    fun assignUser(userNameEntered: String, siteID: Int) = viewModelScope.launch(Dispatchers.IO) {
        _assignUserSharedFlow.value = ApiState.Loading
        usersRepository.assignUser(userNameEntered, siteID.toString()).catch {
            _assignUserSharedFlow.value = ApiState.Failure(it)
        }.collectLatest {
            val response = it
            response.userNameEntered = userNameEntered
            _assignUserSharedFlow.value = ApiState.Success(response)
        }
    }

    fun deAssignUser(userId: String, siteId: String, pos: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _deAssignUserSharedFlow.value = ApiState.Loading
            usersRepository.deAssignUser(userId, siteId).catch {
                _deAssignUserSharedFlow.value = ApiState.Failure(it)
            }.collectLatest {
                val res = it
                res.userId = userId
                res.posToRemove = pos
                _deAssignUserSharedFlow.value = ApiState.Success(res)
            }
        }
}