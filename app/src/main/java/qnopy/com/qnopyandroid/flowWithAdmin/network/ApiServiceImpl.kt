package qnopy.com.qnopyandroid.flowWithAdmin.network

import com.google.gson.Gson
import okhttp3.ResponseBody
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateRequest
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateResponse
import qnopy.com.qnopyandroid.clientmodel.metaForms.FormsData
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model.CreateProjectResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentOrdersListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model.SaveEquipmentsOrderResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel.AssignFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel.GetFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel.GetFormResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.AssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.DeAssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.GenerateTokenResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInResponse
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val apiService: ApiService) {
    suspend fun fetchPdfLogs(pdfLogsRequest: PdfLogsRequest): PdfLogsResponse {
        return apiService.fetchPdfLogs(pdfLogsRequest.siteId, pdfLogsRequest.date)
    }

    suspend fun setFavouriteProject(favouriteProjectRequest: FavouriteProjectRequest): FavouriteProjectResponse {
        return apiService.setFavouriteProject(
            favouriteProjectRequest.siteId,
            favouriteProjectRequest.favouriteStatus
        )
    }

    suspend fun fetchMobileForms(formListRequest: FormListRequest): FormListResponse {
        return apiService.fetchMobileForms(formListRequest)
    }

    suspend fun assignFormToProject(assignFormRequest: AssignFormRequest): MetaFormsJsonResponse {

        val response = apiService.assignFormToProject(assignFormRequest)
        for (forms in response.data.forms) {
            forms.formData = forms.formData//base64 conversion
            forms.formsDetails = Gson().fromJson(forms.formData, FormsData::class.java)
        }
        return response
    }

    suspend fun getForm(getFormRequest: GetFormRequest): GetFormResponse {
        return apiService.getForm(getFormRequest.formId.toString(), getFormRequest)
    }

    suspend fun fetchProjectList(projectListRequest: ProjectListRequest): ProjectListResponse {
        return apiService.fetchProjectList(projectListRequest.keyword)
    }

    suspend fun assignProject(siteId: String): MetaFormsJsonResponse {
        val response = apiService.assignProject(siteId)
        for (forms in response.data.forms) {
            forms.formData = forms.formData//base64 conversion
            forms.formsDetails = Gson().fromJson(forms.formData, FormsData::class.java)
        }
        return response
    }

    suspend fun downloadPdf(fileKeyEncode: String): ResponseBody =
        apiService.downloadPdf(fileKeyEncode)

    suspend fun updateEventName(request: EventNameUpdateRequest): EventNameUpdateResponse {
        return apiService.updateEventName(request)
    }

    // vendorId is siteId
    suspend fun fetchEquipmentOrdersList(vendorId: String): EquipmentOrdersListResponse {
        return apiService.fetchEquipmentOrders(vendorId)
    }

    // vendorId is siteId
    suspend fun saveEquipmentOrder(request: Order): SaveEquipmentsOrderResponse {
        return apiService.saveEquipmentOrder(request)
    }

    suspend fun signIn(token: String, request: SignInRequest): SignInResponse {
        return apiService.signIn(token, request)
    }

    //generate token for signIn api - note this token expired in 10 or 15 mins so hit this api again
    suspend fun getToken(): GenerateTokenResponse {
        return apiService.generateToken()
    }

    suspend fun createProject(request: CreateProjectRequest): CreateProjectResponse {
        return apiService.createProject(request)
    }

    suspend fun assignUser(userNameEntered: String, siteID: String): AssignUserResponse {
        return apiService.assignUser(userNameEntered, siteID)
    }

    suspend fun deAssignUser(userId: String, siteId: String): DeAssignUserResponse {
        return apiService.deAssignUser(userId, siteId)
    }

    suspend fun getAssignedForms(formListRequest: FormListRequest): FormListResponse {
        return apiService.getAssignedForms(
            formListRequest.date.toString(),
            formListRequest.siteId.toString()
        )
    }

    suspend fun getLiveFeed(siteId: String, lastSyncDate: String): LiveFeedResponse {
        return apiService.getLiveFeed(
            siteId, lastSyncDate
        )
    }
}