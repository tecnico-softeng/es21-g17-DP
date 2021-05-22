package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateOpenAnswerQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def courseExecution
    def course

    def teacher
    def student

    def questionDto

    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        questionDto.getQuestionDetailsDto().setCorrectAnswer(ANSWER_1_CONTENT)

    }

    def "user updates an open answer question"() {
        given: "a user"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a question"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openAnswerQuestion = (OpenAnswerQuestionDto) questionDto.getQuestionDetailsDto()

        def mapper = new ObjectMapper()

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200
        and: "check if the correct data is added to database"
        questionRepository.count() == 1L

        def result = response.data
        result.id != null
        result.status == Question.Status.AVAILABLE.name()
        result.title == QUESTION_2_TITLE
        result.content == QUESTION_2_CONTENT
        result.image == null
        result.questionDetailsDto != null

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def "only teachers can update an item combination question"() {
        given: "a student"
        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a question"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        def mapper = new ObjectMapper()

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(student.getId()).get())
    }

    def "only logged teachers can update an open answer question"() {
        given: "a user"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: "a question"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        def mapper = new ObjectMapper()

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())

    }

    def "teacher without permission can't update a question"() {
        given: "a course"
        def course2 = new Course(COURSE_2_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course2)
        def courseExecution2 = new CourseExecution(course2, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution2)

        and: "a teacher"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution2)
        courseExecution2.addUser(teacher)
        userRepository.save(teacher)

        and: "a logged user"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a question"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        def mapper = new ObjectMapper()

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
        questionRepository.deleteAll()
    }
}