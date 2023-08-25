package qnopy.com.qnopyandroid.flowWithAdmin.ui.settings

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.db.AttachmentDataSource
import qnopy.com.qnopyandroid.db.FieldDataSource
import qnopy.com.qnopyandroid.db.MetaDataSource
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.responsemodel.CSVDataModel
import qnopy.com.qnopyandroid.restfullib.AquaBlueService
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SettingRepository @Inject constructor(
    private val filedDataSource: FieldDataSource,
    private val attachDataSource: AttachmentDataSource,
    private val userDataSource: UserDataSource,
    private val metaDataSource: MetaDataSource,
    private val requestQueue: RequestQueue
) {

    val isDataAvailableToSync: Boolean =
        (filedDataSource.isFieldDataAvailableToSync || attachDataSource.attachmentsAvailableToSync())

    fun getUserRole(userName: String): Int = userDataSource.getUserRole(userName)
    fun getDataForCSV(date: String): ArrayList<CSVDataModel> = filedDataSource.getDataForCSV(date)
    fun logoutOpenIdAuth(logoutUrl: String): Flow<String> = flow {
        emit(logoutSession(logoutUrl))
    }.flowOn(Dispatchers.IO)

    private suspend fun logoutSession(logoutUrl: String) = suspendCoroutine { cont ->
        val stringRequest = object : StringRequest(Request.Method.POST, logoutUrl,
            { response ->
                cont.resume(response)
            },
            { cont.resume("Something went wrong while keycloak logout!") }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[GlobalStrings.HEADER_KEY_USER_GUID] = ScreenReso.userDetails.userGuid
                return headers
            }
        }

        requestQueue.add(stringRequest)
    }
}