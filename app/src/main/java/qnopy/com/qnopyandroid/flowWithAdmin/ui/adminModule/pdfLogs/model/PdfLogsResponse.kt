package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model

import com.google.gson.annotations.SerializedName

data class PdfLogsResponse(
    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("responseCode")
    val responseCode: String? = null
)