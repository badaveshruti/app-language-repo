package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.model

data class AssignUserResponse(
    val data: Data?,
    val message: String,
    val responseCode: String,
    val success: Boolean,
    var userNameEntered: String = ""//added for my convenience, this will be the entered name from textview
)