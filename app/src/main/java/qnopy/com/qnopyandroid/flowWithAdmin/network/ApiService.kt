package qnopy.com.qnopyandroid.flowWithAdmin.network

import okhttp3.ResponseBody
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateRequest
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateResponse
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
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model.ProjectListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.AssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model.DeAssignUserResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model.FavouriteProjectResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.GenerateTokenResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls
import retrofit2.http.*

interface ApiService {

    @GET(SubUrls.URL_PDF_LOGS)
    suspend fun fetchPdfLogs(
        @Query("siteId") siteId: String,
        @Query("date") date: String,
    ): PdfLogsResponse

    @PUT(SubUrls.URL_FAVOURITE_PROJECT)
    suspend fun setFavouriteProject(
        @Query("siteId") siteId: String,
        @Query("favouriteStatus") favouriteStatus: String
    ): FavouriteProjectResponse

    @GET(SubUrls.URL_SEARCHED_PROJECT_LIST)
    suspend fun fetchProjectList(@Query("keyword") keyword: String): ProjectListResponse

    @POST(SubUrls.URL_ASSIGN_PROJECT)
    suspend fun assignProject(
        @Query("siteId") siteId: String
    ): MetaFormsJsonResponse

    @POST(SubUrls.URL_FORM_LIST)
    suspend fun fetchMobileForms(@Body formListRequest: FormListRequest): FormListResponse

    @GET(SubUrls.URL_GET_ASSIGNED_PROJECTS)
    suspend fun getAssignedForms(
        @Query("date") userName: String,
        @Query("siteId") siteId: String
    ): FormListResponse

    @POST(SubUrls.URL_ASSIGN_FORM_TO_PROJECT)
    suspend fun assignFormToProject(@Body assignFormRequest: AssignFormRequest): MetaFormsJsonResponse

    @POST(SubUrls.URL_GET_JSON_FORM)
    suspend fun getForm(
        @Query("formId") formId: String,
        @Body getFormRequest: GetFormRequest
    ): GetFormResponse

    @Headers("Content-Type: application/pdf")
    @POST(SubUrls.URL_DOWNLOAD_PDF)
    @Streaming
    suspend fun downloadPdf(@Query("file") fileKeyEncode: String): ResponseBody

    @Headers("Content-Type: application/pdf")
    @POST("${SubUrls.URL_DOWNLOAD_PDF}/{file}")
    @Streaming
    suspend fun downloadPreview(@Query("file") formPreviewKey: String): ResponseBody

    @POST(SubUrls.URL_UPDATE_EVENT_NAME)
    suspend fun updateEventName(
        @Body request: EventNameUpdateRequest
    ): EventNameUpdateResponse

    @GET(SubUrls.URL_GET_EQUIPMENT_ORDERS)
    suspend fun fetchEquipmentOrders(
        @Query("vendorId") vendorId: String
    ): EquipmentOrdersListResponse

    @POST(SubUrls.URL_SAVE_EQUIPMENT_ORDER)
    suspend fun saveEquipmentOrder(
        @Body request: Order
    ): SaveEquipmentsOrderResponse

    @GET(SubUrls.URL_SIGN_IN_TOKEN)
    suspend fun generateToken(): GenerateTokenResponse

    @POST(SubUrls.URL_SIGN_IN)
    suspend fun signIn(
        @Header("token") token: String, @Body request: SignInRequest
    ): SignInResponse

    @POST(SubUrls.URL_CREATE_PROJECT)
    suspend fun createProject(@Body request: CreateProjectRequest): CreateProjectResponse

    @POST(SubUrls.URL_ASSIGN_USER)
    suspend fun assignUser(
        @Query("userName") userName: String,
        @Query("siteId") siteId: String
    ): AssignUserResponse

    @POST(SubUrls.URL_UNASSIGN_USER)
    suspend fun deAssignUser(
        @Query("userId") userName: String,
        @Query("siteId") siteId: String
    ): DeAssignUserResponse

    @GET(SubUrls.URL_GET_LIVE_FEED)
    suspend fun getLiveFeed(
        @Query("siteId") siteId: String,
        @Query("lastSynDate") lastSyncDate: String
    ): LiveFeedResponse
}