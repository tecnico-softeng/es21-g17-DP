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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveItemCombinationQuestionWebServiceIT extends SpockTest {
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
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def itemCombinationOptionDto1 = new ItemCombinationOptionDto()
        def itemCombinationOptionDto2 = new ItemCombinationOptionDto()
        def itemCombinationOptions = new ArrayList<ItemCombinationOptionDto>()

        itemCombinationOptionDto1.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto1.setCorrect(true)
        itemCombinationOptionDto1.setNone(true)
        itemCombinationOptions.add(itemCombinationOptionDto1)

        itemCombinationOptionDto2.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto2.setCorrect(false)
        itemCombinationOptionDto2.setNone(false)
        itemCombinationOptions.add(itemCombinationOptionDto2)

        def item = new ItemCombinationItemDto()

        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        item.setOptions(itemCombinationOptions)
        item.setSequence(1)
        itemCombinationQuestionDto.getItemCombinationItems().add(item)
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)
    }

    def "user removes item combination question"() {
        given: "a user"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        when:
        response = restClient.delete(
                path: '/questions/' + questionDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def "only teachers can remove an item combination question"() {
        given:"a student"
        def student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and:"a questionDto"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        when:
        response = restClient.delete(
                path: '/questions/' + questionDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(student.getId()).get())
    }

    def "only logged teachers can remove an item combination question"() {
        given: "a user"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and:"a questionDto"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)


        when:
        response = restClient.delete(
                path: '/questions/' + questionDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        def exception = thrown(HttpResponseException)
        exception.statusCode == 403

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())

    }

    def "teacher without permission can't remove a question"() {
        given:"a course"
        def course2 = new Course(COURSE_2_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course2)
        def courseExecution2 = new CourseExecution(course2, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution2)

        and:"a teacher"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution2)
        courseExecution2.addUser(teacher)
        userRepository.save(teacher)

        and:"a logged user"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and:"a questionDto"
        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)

        when:
        response = restClient.delete(
                path: '/questions/' + questionDto.getId(),
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