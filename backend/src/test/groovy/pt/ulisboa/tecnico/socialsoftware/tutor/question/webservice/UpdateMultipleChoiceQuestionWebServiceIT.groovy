package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovy.json.JsonOutput
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateMultipleChoiceQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def student
    def teacher
    def course
    def courseExecution
    def questionDto
    def response


    def setup() {

        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)

        and: "a course and course execution"
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: " a questionDto with 2 correct options and no relevancy considered"
        questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def optionDto = new OptionDto()
        def options = new ArrayList<OptionDto>()

        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)

        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)

        def multipleChoiceQuestionDto = new MultipleChoiceQuestionDto()
        questionDto.setQuestionDetailsDto(multipleChoiceQuestionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        and: "create the question in the repository"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)
    }

    def "user updates a multiple choice question with several correct answer"() {
        given: "a teacher"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
        def questionDto2 = new QuestionDto()
        questionDto2.setTitle(QUESTION_2_TITLE)
        questionDto2.setContent(QUESTION_2_CONTENT)

        def optionDto = new OptionDto()
        def options = new ArrayList<OptionDto>()

        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        def multipleChoiceQuestionDto = new MultipleChoiceQuestionDto()
        questionDto2.setQuestionDetailsDto(multipleChoiceQuestionDto)
        questionDto2.getQuestionDetailsDto().setOptions(options)

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: JsonOutput.toJson(questionDto2),
                requestContentType: 'application/json'
        )
        then: "the request returns OK"
        response.status == 200

        and: "the correct data is added to the database"
        questionRepository.count() == 1L

        optionRepository.count() == 2L

        def result = response.data

        result.id != null
        result.title == questionDto2.getTitle()
        result.content == questionDto2.getContent()
        result.status == Question.Status.AVAILABLE.name()
        result.image == null
        result.questionDetailsDto != null
        def optionOneResult = result.questionDetailsDto.options.get(0)
        def optionTwoResult = result.questionDetailsDto.options.get(1)
        optionOneResult.correct
        !optionTwoResult.correct

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())

    }

    def "user cannot update a multiple choice question with no permission"() {
        given: "a student"
        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        and: "logged"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        userRepository.delete(userRepository.findById(student.getId()).get())
    }

    def "only logged teachers can update a multiple choice question"() {
        given: "a not logged teacher"
        teacher = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403
    }

    def "teachers without course access cannot remove a multiple choice question"(){
        given: "another course"
        def anotherCourse = new Course(COURSE_2_NAME, Course.Type.EXTERNAL)
        courseRepository.save(anotherCourse)
        def anotherCourseExecution = new CourseExecution(anotherCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(anotherCourseExecution)

        and: "a teacher"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(anotherCourseExecution)
        anotherCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: "logged"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when:
        response = restClient.put(
                path: '/questions/' + questionDto.getId(),
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def cleanup() {
        persistentCourseCleanup()
        userRepository.deleteAll()
        questionRepository.deleteAll()
    }
}