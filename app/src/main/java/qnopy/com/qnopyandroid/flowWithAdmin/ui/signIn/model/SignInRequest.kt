package qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model

data class SignInRequest(
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val password: String,
    val primaryEmail: String
)