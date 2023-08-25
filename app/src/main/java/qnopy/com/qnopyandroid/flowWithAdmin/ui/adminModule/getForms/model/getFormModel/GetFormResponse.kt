package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel

import com.google.gson.annotations.SerializedName

data class GetFormResponse(

	@field:SerializedName("formId")
	val formId: Int? = null,

	@field:SerializedName("locationStatusQuery")
	val locationStatusQuery: Any? = null,

	@field:SerializedName("appType")
	val appType: Any? = null,

	@field:SerializedName("approvalRequired")
	val approvalRequired: Any? = null,

	@field:SerializedName("jsonKey")
	val jsonKey: Any? = null,

	@field:SerializedName("name")
	val name: Any? = null,

	@field:SerializedName("formTabs")
	val formTabs: List<FormTabsItem?>? = null,

	@field:SerializedName("eventFrequency")
	val eventFrequency: Any? = null
)