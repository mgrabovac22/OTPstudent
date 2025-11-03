package hr.wortex.otpstudent

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import hr.wortex.otpstudent.ui.nav.MainNavGraph
import hr.wortex.otpstudent.ui.theme.OTPstudentTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OTPstudentApp()
        }
    }
}

@Composable
fun OTPstudentApp() {
    OTPstudentTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            MainNavGraph(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OTPstudentAppPreview() {
    OTPstudentApp()
}
