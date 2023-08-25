package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model

data class DeAssignUserResponse(
    val data: Int,
    val message: String,
    val responseCode: String,
    val success: Boolean,
    var userId: String = "0", //added for my convenience
    var posToRemove: Int = -1// added for my convenience
)