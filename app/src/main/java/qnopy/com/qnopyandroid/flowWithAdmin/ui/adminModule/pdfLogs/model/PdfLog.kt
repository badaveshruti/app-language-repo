package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model

import com.google.gson.annotations.SerializedName

data class PdfLog(

    @field:SerializedName("date")
    val date: Long? = null,

    @field:SerializedName("reportName")
    val reportName: String? = null,

    @field:SerializedName("siteId")
    val siteId: Int? = null,

    @field:SerializedName("printLogId")
    val printLogId: Int? = null,

    @field:SerializedName("fileKey")
    val fileKey: String? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("fileFormat")
    val fileFormat: String? = null,

    @field:SerializedName("fileLength")
    val fileLength: Int? = null,

    @field:SerializedName("fileKeyEncode")
    val fileKeyEncode: String? = null,

    val eventName: String? = null
)