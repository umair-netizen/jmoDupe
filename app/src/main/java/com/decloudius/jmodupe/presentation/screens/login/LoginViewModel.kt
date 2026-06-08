package com.decloudius.jmodupe.presentation.screens.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decloudius.jmodupe.domain.model.User
import com.decloudius.jmodupe.domain.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _isEmailValid = mutableStateOf(true)
    val isEmailValid: State<Boolean> = _isEmailValid

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    init {
        checkLoginStatus()
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = newEmail.isEmpty() || isValidEmail(newEmail)
        _loginError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _loginError.value = null
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            _isLoggedIn.value = user != null
            _isLoading.value = false
        }
    }

    fun login() {
        if (_email.value.isBlank() || !_isEmailValid.value || _password.value.isBlank()) {
            return
        }
        viewModelScope.launch {
            val existingUser = userRepository.getUserByEmail(_email.value)
            if (existingUser != null) {
                if (existingUser.password == _password.value) {
                    _isLoggedIn.value = true
                } else {
                    _loginError.value = "Password salah"
                }
            } else {
                val user = User(
                    email = _email.value,
                    password = _password.value,
                    name = _email.value.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                    ktp = "1234567890123456",
                    phone = "081234567890"
                )
                userRepository.insertUser(user)
                _isLoggedIn.value = true
            }
        }
    }
}
