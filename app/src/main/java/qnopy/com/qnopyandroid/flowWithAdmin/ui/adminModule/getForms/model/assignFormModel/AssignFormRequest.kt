package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel

import com.google.gson.annotations.SerializedName

data class AssignFormRequest(

    @field:SerializedName("siteId")
    val siteId: Int? = null,

    @field:SerializedName("formIdList")
    val formIdList: ArrayList<Int> = ArrayList()
)
