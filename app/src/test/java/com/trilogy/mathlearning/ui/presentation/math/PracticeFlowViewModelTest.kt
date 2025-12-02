package com.trilogy.mathlearning.ui.presentation.math

import com.trilogy.mathlearning.MainDispatcherRule
import com.trilogy.mathlearning.data.repository.PracticeRepository
import com.trilogy.mathlearning.domain.model.*
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PracticeFlowViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repo: PracticeRepository
    private lateinit var vm: PracticeFlowViewModel

    @Before
    fun setup() {
        repo = mockk()
        vm = PracticeFlowViewModel(repo)
    }

    // ---------- loadConfig ----------

    @Test
    fun `loadConfig success emits UiState_Success`() = runTest {
        val config = MathConfigResDto(
            curriculum = listOf(
                GradeCurriculumResDto(
                    grade = 6,
                    title = "Lớp 6",
                    chapters = listOf(
                        ChapterResDto(id = 1, title = "Chương 1"),
                        ChapterResDto(id = 2, title = "Chương 2")
                    )
                ),
                GradeCurriculumResDto(
                    grade = 7,
                    title = "Lớp 7",
                    chapters = emptyList()
                )
            )
        )

        coEvery { repo.getMathConfig() } returns NetworkResource.Success(config)

        vm.loadConfig()
        advanceUntilIdle()

        val state = vm.configState.value
        assertTrue(state is UiState.Success)

        val data = (state as UiState.Success).data
        assertEquals(2, data.curriculum.size)
        assertEquals(6, data.curriculum[0].grade)
        assertEquals("Lớp 6", data.curriculum[0].title)
        assertEquals(2, data.curriculum[0].chapters.size)
    }

    @Test
    fun `loadConfig error emits UiState_Failure`() = runTest {
        coEvery { repo.getMathConfig() } returns NetworkResource.Error("Lỗi mạng")

        vm.loadConfig()
        advanceUntilIdle()

        val state = vm.configState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Lỗi mạng", (state as UiState.Failure).error)
    }

    // ---------- createPractice ----------

    @Test
    fun `createPractice success emits UiState_Success`() = runTest {
        val practice = PracticeResDto(
            practiceId = "p1",
            userEmail = "test@example.com",
            grade = 6,
            chapterId = 1,
            exerciseIds = listOf("e1", "e2"),
            correctAnswers = 0,
            totalAnswers = 0,
            score = 0,
            status = PracticeStatus.IN_PROGRESS,
            timeSpent = 0,
            startedAt = null,
            completedAt = null
        )

        coEvery { repo.createPractice(6, 1, "midterm") } returns
                NetworkResource.Success(practice)

        vm.createPractice(grade = 6, chapterId = 1, examType = "midterm")
        advanceUntilIdle()

        val state = vm.createState.value
        assertTrue(state is UiState.Success)
        assertEquals("p1", (state as UiState.Success).data.practiceId)
    }

    @Test
    fun `createPractice error emits UiState_Failure`() = runTest {
        coEvery { repo.createPractice(6, 1, "midterm") } returns
                NetworkResource.Error("Không tạo được bài luyện tập")

        vm.createPractice(grade = 6, chapterId = 1, examType = "midterm")
        advanceUntilIdle()

        val state = vm.createState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Không tạo được bài luyện tập", (state as UiState.Failure).error)
    }

    // ---------- loadExercisesAndBuildExam ----------

    @Test
    fun `loadExercisesAndBuildExam success sets exercisesState and examUi`() = runTest {
        val exercises = listOf(
            ExerciseResDto(
                id = "e1",
                chapterId = 1,
                problem = "1 + 1 = ?",
                difficulty = "easy",
                choices = listOf("1", "2", "3", "4"),
                result = 1,
                solution = "1 + 1 = 2"
            ),
            ExerciseResDto(
                id = "e2",
                chapterId = 1,
                problem = "2 + 2 = ?",
                difficulty = "easy",
                choices = listOf("2", "3", "4", "5"),
                result = 2,
                solution = "2 + 2 = 4"
            )
        )

        coEvery { repo.getExercises("p1") } returns
                NetworkResource.Success(exercises)

        vm.loadExercisesAndBuildExam(practiceId = "p1", examTitle = "Đề ôn tập")
        advanceUntilIdle()

        // exercisesState
        val exState = vm.exercisesState.value
        assertTrue(exState is UiState.Success)
        assertEquals(2, (exState as UiState.Success).data.size)

        // examUi
        val exam = vm.examUi.value
        assertNotNull(exam)
        assertEquals("Đề ôn tập", exam!!.title)
        assertEquals(2, exam.questions.size)
        assertEquals("1 + 1 = ?", exam.questions[0].content)
        assertEquals("A", exam.questions[0].choices[0].label)
        assertEquals("2", exam.questions[0].choices[1].text)

        // selectedAnswers phải được clear và timeSpentSec = 0
        assertTrue(vm.selectedAnswers.isEmpty())
        assertEquals(0, vm.timeSpentSec)
    }

    @Test
    fun `loadExercisesAndBuildExam error emits UiState_Failure`() = runTest {
        coEvery { repo.getExercises("p1") } returns
                NetworkResource.Error("Không tải được danh sách câu hỏi")

        vm.loadExercisesAndBuildExam(practiceId = "p1")
        advanceUntilIdle()

        val state = vm.exercisesState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Không tải được danh sách câu hỏi", (state as UiState.Failure).error)
        assertNull(vm.examUi.value)
    }

    // ---------- select ----------

    @Test
    fun `select stores selected answer index by exerciseId`() = runTest {
        vm.select("e1", 2)
        vm.select("e2", 3)

        assertEquals(2, vm.selectedAnswers["e1"])
        assertEquals(3, vm.selectedAnswers["e2"])
    }

    // ---------- submit ----------

    @Test
    fun `submit builds answers list and calls repository`() = runTest {
        // Giả sử user chọn:
        // e1 -> index 1
        // e2 -> index 3
        vm.select("e1", 1)
        vm.select("e2", 3)
        vm.timeSpentSec = 120 // giả lập đã làm 120 giây

        val practiceResult = PracticeResDto(
            practiceId = "p1",
            userEmail = "test@example.com",
            grade = 6,
            chapterId = 1,
            exerciseIds = listOf("e1", "e2"),
            correctAnswers = 2,
            totalAnswers = 2,
            score = 10,
            status = PracticeStatus.COMPLETED,
            timeSpent = 120,
            startedAt = null,
            completedAt = null
        )

        // Dùng slot để bắt tham số answers mà ViewModel gửi xuống repo
        val answersSlot = slot<List<SubmitAnswerReqDto>>()

        coEvery {
            repo.submitPractice("p1", 120, capture(answersSlot))
        } returns NetworkResource.Success(practiceResult)

        vm.submit("p1")
        advanceUntilIdle()

        // Kiểm tra submitState
        val state = vm.submitState.value
        assertTrue(state is UiState.Success)
        assertEquals("p1", (state as UiState.Success).data.practiceId)

        // Kiểm tra list answers được build đúng
        val sentAnswers = answersSlot.captured
        assertEquals(2, sentAnswers.size)
        // Do selectedAnswers là map nên thứ tự có thể khác, kiểm tra theo id
        val e1 = sentAnswers.first { it.exerciseId == "e1" }
        val e2 = sentAnswers.first { it.exerciseId == "e2" }
        assertEquals(1, e1.userAnswer)
        assertEquals(3, e2.userAnswer)

        // Verify repo được gọi đúng
        coVerify(exactly = 1) { repo.submitPractice("p1", 120, any()) }
    }

    @Test
    fun `submit error emits UiState_Failure`() = runTest {
        vm.select("e1", 1)
        vm.timeSpentSec = 60

        coEvery {
            repo.submitPractice("p1", 60, any())
        } returns NetworkResource.Error("Nộp bài thất bại")

        vm.submit("p1")
        advanceUntilIdle()

        val state = vm.submitState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Nộp bài thất bại", (state as UiState.Failure).error)
    }

    // ---------- loadHistory ----------

    @Test
    fun `loadHistory success emits UiState_Success`() = runTest {
        val practice = PracticeResDto(
            practiceId = "p1",
            userEmail = "test@example.com",
            grade = 6,
            chapterId = 1,
            exerciseIds = listOf("e1", "e2"),
            correctAnswers = 1,
            totalAnswers = 2,
            score = 5,
            status = PracticeStatus.COMPLETED,
            timeSpent = 100,
            startedAt = null,
            completedAt = null
        )
        val history = ListPracticesResDto(
            practices = listOf(practice),
            total = 1
        )

        coEvery { repo.listPractices() } returns
                NetworkResource.Success(history)

        vm.loadHistory()
        advanceUntilIdle()

        val state = vm.historyState.value
        assertTrue(state is UiState.Success)
        assertEquals(1, (state as UiState.Success).data.total)
    }

    @Test
    fun `loadHistory error emits UiState_Failure`() = runTest {
        coEvery { repo.listPractices() } returns
                NetworkResource.Error("Không tải được lịch sử")

        vm.loadHistory()
        advanceUntilIdle()

        val state = vm.historyState.value
        assertTrue(state is UiState.Failure)
        assertEquals("Không tải được lịch sử", (state as UiState.Failure).error)
    }

    // ---------- resetAllForNewPractice ----------

    @Test
    fun `resetAllForNewPractice clears states and selections`() = runTest {
        // setup dummy data
        vm.select("e1", 1)
        vm.timeSpentSec = 50
        vm.resetAllForNewPractice()

        assertTrue(vm.selectedAnswers.isEmpty())
        assertEquals(0, vm.timeSpentSec)
        assertTrue(vm.createState.value is UiState.Empty)
        assertTrue(vm.exercisesState.value is UiState.Empty)
        assertTrue(vm.submitState.value is UiState.Empty)
        assertNull(vm.examUi.value)
    }
}
