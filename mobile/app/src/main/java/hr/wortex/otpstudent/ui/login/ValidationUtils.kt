package hr.wortex.otpstudent.ui.login


object ValidationUtils {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}
