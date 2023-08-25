package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.model

import com.google.gson.annotations.SerializedName

data class FavouriteProjectResponse(

    @field:SerializedName("data")
    val data: Int? = null,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("responseCode")
    val responseCode: String? = null
)