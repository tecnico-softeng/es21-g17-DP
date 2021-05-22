package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Shared
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_COURSE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUBMISSION_MISSING_STUDENT

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOpenAnswerQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port
    
    def courseExecution
    def course
    def teacher
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

    def "question creation by teacher with permission"() {
        given: "a teacher"
        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)
        createdUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        def mapper = new ObjectMapper()

        when:
        response = restClient.post(
                path: '/courses/' + courseExecution.getId() + '/questions',
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        then: "check the database"
        def result = response.data
        result.id != null
        result.status == Question.Status.AVAILABLE.name()
        result.title == QUESTION_1_TITLE
        result.content == QUESTION_1_CONTENT
        result.image == null
        result.questionDetailsDto != null

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def "teacher must have permissions to create an open answer question"() {
        given: "a teacher"
        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        def mapper = new ObjectMapper()

        when:
        response = restClient.post(
                path: '/courses/' + courseExecution.getId() + '/questions',
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"

        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def "open answer question can only be created by teachers"() {
        given: "a student"
        def student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def mapper = new ObjectMapper()

        when:
        response = restClient.post(
                path: '/courses/' + courseExecution.getId() + '/questions',
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"

        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(student.getId()).get())
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
        questionRepository.deleteAll()
    }
}
