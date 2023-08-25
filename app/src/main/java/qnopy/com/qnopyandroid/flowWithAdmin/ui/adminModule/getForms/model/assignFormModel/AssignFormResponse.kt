package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel

import com.google.gson.annotations.SerializedName

data class AssignFormResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("responseCode")
    val responseCode: String
)