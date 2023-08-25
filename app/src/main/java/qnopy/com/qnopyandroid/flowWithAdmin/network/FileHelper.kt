package qnopy.com.qnopyandroid.flowWithAdmin.network

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object FileHelper {

    fun createRequestBody(file: File): RequestBody {
        return file.asRequestBody("image/*".toMediaTypeOrNull())
    }

    fun createTextRequestBody(value: String): RequestBody {
        return value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    fun createPart(file: File, requestBody: RequestBody, paramName: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    }
}