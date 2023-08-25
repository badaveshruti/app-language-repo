package qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val signInRepository: SignInRepository) :
    ViewModel() {

    private val _getToken: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val getTokenState: StateFlow<ApiState> = _getToken

    private val _signInState: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val signInState: StateFlow<ApiState> = _signInState

    fun signIn(token: String, signInRequest: SignInRequest) {
        viewModelScope.launch {
            _signInState.value = ApiState.Loading
            signInRepository.signIn(token, signInRequest).catch {
                _signInState.value = ApiState.Failure(it)
            }.collect {
                _signInState.value = ApiState.Success(it)
            }
        }
    }

    fun genToken() {
        viewModelScope.launch {
            _getToken.value = ApiState.Loading
            signInRepository.generateToken().catch {
                _getToken.value = ApiState.Failure(it)
            }.collect {
                _getToken.value = ApiState.Success(it)
            }
        }
    }
}