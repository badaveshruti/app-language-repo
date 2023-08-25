package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel

import com.google.gson.annotations.SerializedName

data class Data(

    @field:SerializedName("lastFetchDate")
    val lastFetchDate: Long? = null,

    @field:SerializedName("list")
    val formList: ArrayList<Form> = arrayListOf()
)