package qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model

data class SignInResponse(
    val `data`: Data,
    val message: String,
    val responseCode: String,
    val success: Boolean
)