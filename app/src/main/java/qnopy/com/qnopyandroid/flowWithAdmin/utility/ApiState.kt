package qnopy.com.qnopyandroid.flowWithAdmin.utility

sealed class ApiState {
    object Loading : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    class Success(val response: Any) : ApiState()
    object Empty : ApiState()
}
