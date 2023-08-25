package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Data(

    @field:SerializedName("printLogList")
    val printLogList: MutableList<PdfLog?>? = null,

    @field:SerializedName("lastSyncDate")
    val lastSyncDate: Long? = null
)