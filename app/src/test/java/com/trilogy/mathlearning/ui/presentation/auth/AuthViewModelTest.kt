package com.trilogy.mathlearning.ui.presentation.auth

import android.content.SharedPreferences
import android.util.Log
import com.trilogy.mathlearning.MainDispatcherRule
import com.trilogy.mathlearning.data.repository.AuthRepository
import com.trilogy.mathlearning.data.repository.UserRepository
import com.trilogy.mathlearning.domain.model.*
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.SharedPreferencesReManager
import com.trilogy.mathlearning.utils.UiState
import com.trilogy.mathlearning.utils.myUser
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepo: AuthRepository
    private lateinit var userRepo: UserRepository
    private lateinit var vm: AuthViewModel

    // -------- In-memory SharedPreferences để khỏi crash prefs -----------
    private class InMemorySharedPreferences : SharedPreferences {

        private val data = mutableMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = data

        override fun getString(key: String?, defValue: String?): String? =
            data[key] as? String ?: defValue

        @Suppress("UNCHECKED_CAST")
        override fun getStringSet(
            key: String?,
            defValues: MutableSet<String>?
        ): MutableSet<String>? = data[key] as? MutableSet<String> ?: defValues

        override fun getInt(key: String?, defValue: Int): Int =
            data[key] as? Int ?: defValue

        override fun getLong(key: String?, defValue: Long): Long =
            data[key] as? Long ?: defValue

        override fun getFloat(key: String?, defValue: Float): Float =
            data[key] as? Float ?: defValue

        override fun getBoolean(key: String?, defValue: Boolean): Boolean =
            data[key] as? Boolean ?: defValue

        override fun contains(key: String?): Boolean = data.containsKey(key)

        override fun edit(): SharedPreferences.Editor = EditorImpl()

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) { }

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) { }

        private inner class EditorImpl : SharedPreferences.Editor {

            private val pending = mutableMapOf<String, Any?>()
            private var clearAll = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                if (key != null) pending[key] = value
                return this
            }

            override fun putStringSet(
                key: String?,
                values: MutableSet<String>?
            ): SharedPreferences.Editor {
                if (key != null) pending[key] = values
                return this
            }

            override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
                if (key != null) pending[key] = value
                return this
            }

            override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
                if (key != null) pending[key] = value
                return this
            }

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                if (key != null) pending[key] = value
                return this
            }

            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
                if (key != null) pending[key] = value
                return this
            }

            override fun remove(key: String?): SharedPreferences.Editor {
                if (key != null) pending[key] = Unit
                return this
            }

            override fun clear(): SharedPreferences.Editor {
                clearAll = true
                return this
            }

            override fun commit(): Boolean {
                if (clearAll) data.clear()
                pending.forEach { (k, v) ->
                    if (v === Unit) data.remove(k) else data[k] = v
                }
                return true
            }

            override fun apply() {
                commit()
            }
        }
    }

    // --------------------------------------------------------------------
    @Before
    fun setup() {
        // gắn prefs giả vào SharedPreferencesReManager.prefs
        val prefsField =
            SharedPreferencesReManager::class.java.getDeclaredField("prefs")
        prefsField.isAccessible = true
        prefsField.set(null, InMemorySharedPreferences())

        // mock Log.* để khỏi lỗi "Method d in android.util.Log not mocked"
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0

        authRepo = mockk<AuthRepository>()
        userRepo = mockk<UserRepository>()
        vm = AuthViewModel(authRepo, userRepo)

        myUser = null
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkStatic(Log::class)
        myUser = null
    }

    // ====================================================================
    // 1. LOGIN SUCCESS
    // ====================================================================
    @Test
    fun loginSuccess_emitsSuccessAndLoadsUser() = runTest {
        val email = "test@example.com"
        val password = "123456"

        val loginRes = LoginResDto(
            accessToken = "access-token-123",
            refreshToken = "refresh-token-456"
        )

        val userRes = UserResDto(
            id = "u1",
            email = email,
            name = "Tester",
            point = 10,
            like = 5,
            lastLogin = 123L
        )

        // Kotlin sẽ suy luận T từ type NetworkResource<LoginResDto>
        val loginResource: NetworkResource<LoginResDto> =
            NetworkResource.Success(loginRes)

        val userResource: NetworkResource<UserResDto> =
            NetworkResource.Success(userRes)

        coEvery { authRepo.login(LoginDto(email, password)) } returns loginResource
        coEvery { userRepo.getUser() } returns userResource

        vm.login(email, password)
        advanceUntilIdle()

        val state = vm.authState.value
        assertTrue(state is UiState.Success<*>)

        val data = (state as UiState.Success<*>).data as LoginResDto
        assertEquals("access-token-123", data.accessToken)

        assertNotNull(vm.userInfo.value)
        assertEquals(email, vm.userInfo.value?.email)

        coVerify(exactly = 1) { authRepo.login(LoginDto(email, password)) }
        coVerify(exactly = 1) { userRepo.getUser() }
    }

    // ====================================================================
    // 2. LOGIN ERROR
    // ====================================================================
    @Test
    fun loginError_emitsFailureWithMessage() = runTest {
        val email = "test@example.com"
        val password = "wrong"

        val errorResource: NetworkResource<LoginResDto> =
            NetworkResource.Error("Login failed")

        coEvery { authRepo.login(LoginDto(email, password)) } returns errorResource

        vm.login(email, password)
        advanceUntilIdle()

        val state = vm.authState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Login failed", (state as UiState.Failure).error)
    }

    // ====================================================================
    // 3. LOGOUT
    // ====================================================================
    @Test
    fun logout_clearsUserAndStates() = runTest {
        vm.logout()
        advanceUntilIdle()

        assertNull(vm.userInfo.value)
        assertTrue(vm.authState.value is UiState.Empty)
        assertTrue(vm.forgotPasswordState.value is UiState.Empty)
        assertTrue(vm.resetPasswordState.value is UiState.Empty)
    }
}
