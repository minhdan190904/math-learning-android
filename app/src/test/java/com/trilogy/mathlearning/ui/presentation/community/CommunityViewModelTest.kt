package com.trilogy.mathlearning.ui.presentation.community

import com.trilogy.mathlearning.MainDispatcherRule
import com.trilogy.mathlearning.data.repository.CommunityRepository
import com.trilogy.mathlearning.domain.model.AnswerResDto
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repo: CommunityRepository
    private lateinit var vm: CommunityViewModel

    @Before
    fun setup() {
        // Mock repository để không gọi API thật
        repo = mockk()
        vm = CommunityViewModel(repo)
    }

    // 1. Validation: không nhập nội dung + không có ảnh -> Failure và không gọi repo
    @Test
    fun `createQuestion empty content and image returns Failure`() = runTest {
        vm.createQuestion(content = null, imageUrl = null)

        val state = vm.postState.value
        assertTrue(state is UiState.Failure)
        assertEquals(
            "Vui lòng nhập nội dung hoặc đính kèm ảnh",
            (state as UiState.Failure).error
        )

        coVerify(exactly = 0) { repo.createQuestion(any(), any()) }
    }

    // 2. Tạo câu hỏi thành công -> UiState Success
    @Test
    fun `createQuestion with valid content emits Success`() = runTest {
        val question = QuestionResDto(
            id = "q1",
            authorEmail = "test@example.com",
            authorName = "Tester",
            content = "Câu hỏi test",
            imageUrl = null,
            topic = "math",
            answers = emptyList()
        )

        coEvery { repo.createQuestion("Câu hỏi test", null) } returns
                NetworkResource.Success(question)

        vm.createQuestion(content = "Câu hỏi test", imageUrl = null)
        advanceUntilIdle()

        val state = vm.postState.value
        assertTrue(state is UiState.Success)
        val data = (state as UiState.Success).data
        assertEquals("q1", data.id)
        assertEquals("Câu hỏi test", data.content)
    }

    // 3. Load danh sách câu hỏi thành công
    @Test
    fun `loadQuestions emits Success with list`() = runTest {
        val q1 = QuestionResDto(
            id = "q1",
            authorEmail = "a@b.com",
            authorName = "A",
            content = "Hello",
            imageUrl = null,
            topic = "math",
            answers = emptyList()
        )
        val q2 = q1.copy(id = "q2", content = "Hi")

        coEvery { repo.loadQuestions() } returns
                NetworkResource.Success(listOf(q1, q2))

        vm.loadQuestions()
        advanceUntilIdle()

        val state = vm.questionsState.value
        assertTrue(state is UiState.Success)
        val list = (state as UiState.Success).data
        assertEquals(2, list.size)
        assertEquals("Hello", list[0].content)
    }

    // 4. Load danh sách bị lỗi -> UiState Failure
    @Test
    fun `loadQuestions error emits Failure`() = runTest {
        coEvery { repo.loadQuestions() } returns
                NetworkResource.Error("Lỗi mạng")

        vm.loadQuestions()
        advanceUntilIdle()

        val state = vm.questionsState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Lỗi mạng", (state as UiState.Failure).error)
    }

    // 5. Load chi tiết câu hỏi thành công
    @Test
    fun `loadQuestionDetail emits Success with question`() = runTest {
        val detail = QuestionResDto(
            id = "q1",
            authorEmail = "owner@example.com",
            authorName = "Owner",
            content = "Chi tiết",
            imageUrl = null,
            topic = "math",
            answers = emptyList()
        )

        coEvery { repo.loadQuestionDetail("q1") } returns
                NetworkResource.Success(detail)

        vm.loadQuestionDetail("q1")
        advanceUntilIdle()

        val state = vm.detailState.value
        assertTrue(state is UiState.Success)
        assertEquals("q1", (state as UiState.Success).data.id)
    }

    // 6. Load chi tiết bị lỗi -> UiState Failure
    @Test
    fun `loadQuestionDetail error emits Failure`() = runTest {
        coEvery { repo.loadQuestionDetail("not_found") } returns
                NetworkResource.Error("Không tìm thấy")

        vm.loadQuestionDetail("not_found")
        advanceUntilIdle()

        val state = vm.detailState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Không tìm thấy", (state as UiState.Failure).error)
    }

    // 7. createAnswer -> gọi repo và reload detail
    @Test
    fun `createAnswer calls repo and reloads detail`() = runTest {
        // Khi tạo answer
        coEvery { repo.createAnswer("q1", "Hello", null, any()) } returns
                NetworkResource.Success(
                    AnswerResDto(
                        id = "a1",
                        questionId = "q1",
                        authorEmail = "u@example.com",
                        authorName = "User",
                        content = "Hello",
                        imageUrl = null,
                        likedBy = emptyList(),
                        likes = 0,
                        isAI = false
                    )
                )

        // Sau đó reload detail
        val detail = QuestionResDto(
            id = "q1",
            authorEmail = "owner@example.com",
            authorName = "Owner",
            content = "Chi tiết",
            imageUrl = null,
            topic = "math",
            answers = emptyList()
        )
        coEvery { repo.loadQuestionDetail("q1") } returns
                NetworkResource.Success(detail)

        vm.createAnswer("q1", "Hello", null)
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.createAnswer("q1", "Hello", null, false) }
        coVerify(exactly = 1) { repo.loadQuestionDetail("q1") }

        val state = vm.detailState.value
        assertTrue(state is UiState.Success)
        assertEquals("q1", (state as UiState.Success).data.id)
    }

    // 8. toggleLike chỉ cần đảm bảo gọi repo.toggleLike
    @Test
    fun `toggleLike calls repository`() = runTest {
        coEvery { repo.toggleLike("a1") } returns
                NetworkResource.Success(
                    AnswerResDto(
                        id = "a1",
                        questionId = "q1",
                        authorEmail = "u@example.com",
                        authorName = "User",
                        content = "Hi",
                        imageUrl = null,
                        likedBy = emptyList(),
                        likes = 1,
                        isAI = false
                    )
                )

        vm.toggleLike("a1")
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.toggleLike("a1") }
    }
}
