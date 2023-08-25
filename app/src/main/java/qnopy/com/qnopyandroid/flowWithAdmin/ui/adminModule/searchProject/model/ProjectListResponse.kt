package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model

import com.google.gson.annotations.SerializedName

data class ProjectListResponse(

    @field:SerializedName("data")
    val data: ArrayList<Project>? = null,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("responseCode")
    val responseCode: String? = null
)