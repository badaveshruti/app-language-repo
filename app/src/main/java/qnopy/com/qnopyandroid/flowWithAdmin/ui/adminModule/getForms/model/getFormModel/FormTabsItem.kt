package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.getFormModel

import com.google.gson.annotations.SerializedName

data class FormTabsItem(

	@field:SerializedName("tabId")
	val tabId: Int? = null,

	@field:SerializedName("headerFlag")
	val headerFlag: Int? = null,

	@field:SerializedName("tabName")
	val tabName: String? = null,

	@field:SerializedName("appType")
	val appType: String? = null,

	@field:SerializedName("tabOrder")
	val tabOrder: Int? = null,

	@field:SerializedName("display")
	val display: Any? = null,

	@field:SerializedName("appDescription")
	val appDescription: Any? = null,

	@field:SerializedName("allowMultipleSets")
	val allowMultipleSets: Boolean? = null,

	@field:SerializedName("fields")
	val fields: List<FieldsItem?>? = null
)