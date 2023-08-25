package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectResponse
import javax.inject.Inject

class CreateProjectRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    fun createProject(request: CreateProjectRequest): Flow<CreateProjectResponse> =
        flow {
            emit(apiServiceImpl.createProject(request))
        }.flowOn(Dispatchers.IO)
}