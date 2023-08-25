package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListResponse
import javax.inject.Inject

class SearchProjectRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    fun fetchProjectList(projectListRequest: ProjectListRequest): Flow<ProjectListResponse> = flow {
        emit(apiServiceImpl.fetchProjectList(projectListRequest))
    }.flowOn(Dispatchers.IO)

    fun assignProject(siteId: String): Flow<MetaFormsJsonResponse> = flow {
        emit(apiServiceImpl.assignProject(siteId))
    }.flowOn(Dispatchers.IO)
}