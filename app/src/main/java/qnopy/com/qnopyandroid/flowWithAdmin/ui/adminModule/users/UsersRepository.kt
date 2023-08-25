package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.AssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.DeAssignUserResponse
import qnopy.com.qnopyandroid.requestmodel.SUser
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val userDataSource: UserDataSource,
    private val apiServiceImpl: ApiServiceImpl
) {
    fun getUsersListForSite(userId: String, companyId: Int, siteId: Int): ArrayList<SUser> =
        userDataSource.getUsersForAdmin(siteId)

    fun assignUser(userNameEntered: String, siteID: String): Flow<AssignUserResponse> = flow {
        emit(apiServiceImpl.assignUser(userNameEntered, siteID))
    }.flowOn(Dispatchers.IO)

    fun deAssignUser(userId: String, siteId: String): Flow<DeAssignUserResponse> =
        flow<DeAssignUserResponse> {
            emit(apiServiceImpl.deAssignUser(userId, siteId))
        }.flowOn(Dispatchers.IO)
}