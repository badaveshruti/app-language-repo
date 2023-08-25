package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel

import com.google.gson.annotations.SerializedName

data class FormsItem(

	@field:SerializedName("formId")
	val formId: Int? = null,

	@field:SerializedName("formData")
	val formData: String? = null
)