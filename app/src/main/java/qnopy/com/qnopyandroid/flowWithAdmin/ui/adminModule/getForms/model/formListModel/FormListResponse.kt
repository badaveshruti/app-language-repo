package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel

import com.google.gson.annotations.SerializedName

data class FormListResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("success")
    val success: Boolean = false,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("responseCode")
    val responseCode: String? = null
)