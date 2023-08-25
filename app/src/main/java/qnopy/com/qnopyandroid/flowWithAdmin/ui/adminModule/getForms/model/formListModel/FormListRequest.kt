package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel

import com.google.gson.annotations.SerializedName

data class FormListRequest(

	@field:SerializedName("date")
	val date: Int,

	@field:SerializedName("siteId")
	val siteId: Int
)
