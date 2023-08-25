package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("forms")
	val forms: List<FormsItem?>? = null
)