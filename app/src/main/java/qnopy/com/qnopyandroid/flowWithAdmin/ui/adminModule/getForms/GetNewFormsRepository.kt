package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel.AssignFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel.GetFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel.GetFormResponse
import javax.inject.Inject

class GetNewFormsRepository @Inject constructor(
    private val apiServiceImpl: ApiServiceImpl) {

    fun fetchMobileForms(formListRequest: FormListRequest): Flow<FormListResponse> = flow {
        emit(apiServiceImpl.fetchMobileForms(formListRequest))
    }.flowOn(Dispatchers.IO)

    fun downloadFormPreview(form: Form): Flow<ResponseBody> = flow<ResponseBody> {
        form.formPreview?.let {
            emit(apiServiceImpl.downloadPdf(it))
        }
    }.flowOn(Dispatchers.IO)

    fun assignFormToProject(assignFormRequest: AssignFormRequest): Flow<MetaFormsJsonResponse> =
        flow {
            emit(apiServiceImpl.assignFormToProject(assignFormRequest))
        }.flowOn(Dispatchers.IO)

    fun getAssignedForm(request: FormListRequest): Flow<FormListResponse> = flow {
        emit(apiServiceImpl.getAssignedForms(request))
    }.flowOn(Dispatchers.IO)

}