package qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import org.springframework.core.io.FileSystemResource
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateRequest
import qnopy.com.qnopyandroid.clientmodel.EventNameUpdateResponse
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl
import java.io.File
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(
    private val mAquaBlueServiceImpl: AquaBlueServiceImpl,
    private val apiServiceImpl: ApiServiceImpl
) {

    fun syncMedia(
        jsonObject: JSONObject?,
        absolutePath: String,
        baseUrl: String,
        subUrl: String
    ): Flow<AttachmentTaskResponseModel> = flow {
        emit(syncTaskMedia(jsonObject, absolutePath, baseUrl, subUrl))
    }.flowOn(Dispatchers.IO)

    private fun syncTaskMedia(
        jsonObject: JSONObject?, absolutePath: String, baseUrl: String,
        subUrl: String
    ): AttachmentTaskResponseModel {
        val files: MultiValueMap<String, Any> = LinkedMultiValueMap()

        val file = File(absolutePath)
        try {
            if (file.exists()) {
                files.add("files", FileSystemResource(file))
            }
        } catch (n: NullPointerException) {
            n.printStackTrace()
        }
        files.add("media", jsonObject.toString())
        val resultModel = mAquaBlueServiceImpl.TaskMediaUpload(
            baseUrl, subUrl,
            files
        )
        return if (resultModel != null) {
            resultModel
        } else {
            Log.e("imageUpload", "doInBackground: fails to upload image attachment")
            AttachmentTaskResponseModel()
        }
    }

    fun sendReport(
        forPM: Boolean,
        pdf: Boolean,
        mEvent: EventData,
        forSelf: Boolean, baseUrl: String, subUrl: String
    ): Flow<String> = flow<String> {
        emit(
            mAquaBlueServiceImpl.generateReport(
                baseUrl,
                subUrl,
                mEvent.siteID.toString(),
                mEvent.eventID.toString(),
                mEvent.mobAppID.toString(),
                mEvent.userId.toString(),
                forPM,
                pdf,
                forSelf
            )
        )
    }.flowOn(Dispatchers.IO)

    fun updateEventName(
        request: EventNameUpdateRequest
    ): Flow<EventNameUpdateResponse> =
        flow {
            emit(
                apiServiceImpl.updateEventName(request)
            )
        }.flowOn(Dispatchers.IO)
}