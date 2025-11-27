package hr.wortex.otpstudent.ui.registration.utils

object ValidationUtils {
    fun isLongerThanOneCharacter(input: String): Boolean{
        return input.length > 1
    }

    fun isEmpty(input: String): Boolean{
        return input.isBlank()
    }

    fun isEmailValid(email: String): Boolean{
        val regex = Regex("[a-zA-Z0-9]+@student.foi.hr$")

        return regex.matches(email)
    }

    fun isPasswordValid(password: String): Boolean{
        val regex = Regex("^[a-zA-Z0-9!@#$%-_+*&]{8,64}$")

        return regex.matches(password)
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean{
        return password == confirmPassword
    }
}