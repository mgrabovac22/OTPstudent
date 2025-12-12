package hr.wortex.otpstudent.ui.registration.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())

    fun formatForApi(displayString: String): String {
        return try {
            val date = displayFormat.parse(displayString)
            if (date != null) apiFormat.format(date) else displayString
        } catch (e: Exception) {
            displayString
        }
    }

    fun todayMillis(): Long = Calendar.getInstance().timeInMillis

    fun isAtLeast18(displayDate: String): Boolean {
        return try {
            val dob = displayFormat.parse(displayDate) ?: return false
            val calDob = Calendar.getInstance().apply { time = dob }
            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - calDob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < calDob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age >= 18
        } catch (e: Exception) {
            false
        }
    }

    fun isNotInFuture(displayDate: String): Boolean {
        return try {
            val dob = displayFormat.parse(displayDate) ?: return false
            dob.time <= todayMillis()
        } catch (e: Exception) {
            false
        }
    }

    fun latestAllowedBirthDateMillis(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -18)
        return cal.timeInMillis
    }
}