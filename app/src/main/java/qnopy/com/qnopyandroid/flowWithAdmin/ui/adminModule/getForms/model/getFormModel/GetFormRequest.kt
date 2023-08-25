package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel

import com.google.gson.annotations.SerializedName

data class GetFormRequest(

	@field:SerializedName("formId")
	val formId: Int? = null,

	@field:SerializedName("lastFetchDate")
	val lastFetchDate: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)