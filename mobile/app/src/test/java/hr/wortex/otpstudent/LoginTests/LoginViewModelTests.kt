package hr.wortex.otpstudent.ui.login

import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.usecase.Login
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var loginUseCase: Login
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- VALIDACIJA E-MAIL ---
    @Test
    fun `validateEmail returns error when blank`() {
        val result = viewModel.validateEmail("")
        assertEquals("Potrebno je unijeti Email adresu", result)
    }

    @Test
    fun `validateEmail returns error when invalid format`() {
        val result = viewModel.validateEmail("invalidemail.com")
        assertEquals("Neispravan format Email adrese", result)
    }

    @Test
    fun `validateEmail returns null when valid`() {
        val result = viewModel.validateEmail("mbanovic21@student.foi.hr")
        assertNull(result)
    }

    // --- VALIDACIJA LOZINKE ---
    @Test
    fun `validatePassword returns error when blank`() {
        val result = viewModel.validatePassword("")
        assertEquals("Potrebno je unijeti lozinku", result)
    }

    @Test
    fun `validatePassword returns error when too short`() {
        val result = viewModel.validatePassword("123")
        assertEquals("Lozinka mora imati barem 6 znakova", result)
    }

    @Test
    fun `validatePassword returns null when valid`() {
        val result = viewModel.validatePassword("password123")
        assertNull(result)
    }

    // --- LOGIN UI STATE ---
    @Test
    fun `loginUser sets Error when login fails with HttpException`() = runTest {
        coEvery { loginUseCase("wrong@test.com", "password") } throws HttpException(
            mockk(relaxed = true)
        )

        viewModel.loginUser("wrong@test.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assert(state is LoginUiState.Error)
        assertNotNull((state as LoginUiState.Error).message)
    }

    @Test
    fun `loginUser sets Error when login fails with IOException`() = runTest {
        coEvery { loginUseCase("any@test.com", "password") } throws IOException("No internet")

        viewModel.loginUser("any@test.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assert(state is LoginUiState.Error)
        assertEquals("Nema internetske veze. Provjerite mrežu.", (state as LoginUiState.Error).message)
    }
}
