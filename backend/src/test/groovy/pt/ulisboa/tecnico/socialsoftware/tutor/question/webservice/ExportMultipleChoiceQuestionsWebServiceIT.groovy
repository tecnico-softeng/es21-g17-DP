package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportMultipleChoiceQuestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def user
    def questionDto
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def multipleChoiceQuestionDto = new MultipleChoiceQuestionDto()
        multipleChoiceQuestionDto.setRelevanceOfOptionsConsidered(true)
        questionDto.setQuestionDetailsDto(multipleChoiceQuestionDto)

        def options = new ArrayList<OptionDto>()

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(1)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)

        questionDto = questionRepository.save(new Question(course, questionDto))
    }

    def "export a multiple choice question with several correct answers"() {
        given: "a teacher of that course"
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when: "the teacher tries to export a question"
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export/',
                requestContentType: 'application/json'
        )

        then: "it is successful"
        assert response['response'].status == 200
        //response != null
        //response.status == 200
    }

    def "teacher without course access cannot export a multiple choice question"() {
        given: "a teacher without course executions"
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(user)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: "the teacher tries to export a question"
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export/',
                requestContentType: 'application/json'
        )

        then: "the teacher doesnt have permission"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403
    }

    def "only teachers can export a multiple choice question"() {
        given: "a student"
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(user)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: "the student tries to export a question"
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export/',
                requestContentType: 'application/json'
        )

        then: "the student doesnt have permission"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403
    }

    def "non authenticated users cannot export a multiple choice question"() {
        given: "a non authenticated user"
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(user)

        when: "the user tries to export a question"
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export/',
                requestContentType: 'application/json'
        )

        then: "the user doesnt have permission"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403
    }

    def cleanup() {
        persistentCourseCleanup()
        userRepository.deleteById(user.getId())
        questionRepository.deleteAll()
    }
}
