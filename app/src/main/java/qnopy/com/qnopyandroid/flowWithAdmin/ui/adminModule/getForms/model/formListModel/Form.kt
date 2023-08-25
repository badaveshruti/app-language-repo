package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel

import com.google.gson.annotations.SerializedName

data class Form(
    @field:SerializedName("formId")
    var formId: Int = 0,

    @field:SerializedName("companyId")
    var companyId: Int? = null,

    @field:SerializedName("formName")
    var formName: String? = null,

    @field:SerializedName("time")
    var time: Long? = null,

    @field:SerializedName("form_preview")
    var formPreview: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    var getNewForm: Boolean = false//added for my convenience
)