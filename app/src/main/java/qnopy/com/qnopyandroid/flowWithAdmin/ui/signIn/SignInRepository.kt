package qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.GenerateTokenResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInResponse
import javax.inject.Inject

class SignInRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {
    fun generateToken(): Flow<GenerateTokenResponse> = flow {
        emit(apiServiceImpl.getToken())
    }.flowOn(Dispatchers.IO)

    fun signIn(token: String, signInRequest: SignInRequest): Flow<SignInResponse> = flow {
        emit(apiServiceImpl.signIn(token, signInRequest))
    }.flowOn(Dispatchers.IO)
}