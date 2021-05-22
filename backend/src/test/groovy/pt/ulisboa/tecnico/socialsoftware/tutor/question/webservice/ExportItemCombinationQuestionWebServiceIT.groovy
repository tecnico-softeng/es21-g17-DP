package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

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
class ExportItemCombinationQuestionWebServiceIT extends SpockTest {
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

        def itemCombinationOptionDto1 = new ItemCombinationOptionDto()
        def itemCombinationOptionDto2 = new ItemCombinationOptionDto()
        def itemCombinationOptions = new ArrayList<ItemCombinationOptionDto>()

        itemCombinationOptionDto1.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto1.setCorrect(true)
        itemCombinationOptionDto1.setNone(true)
        itemCombinationOptionDto2.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto2.setCorrect(false)
        itemCombinationOptionDto2.setNone(false)
        itemCombinationOptions.add(itemCombinationOptionDto1)
        itemCombinationOptions.add(itemCombinationOptionDto2)

        def item = new ItemCombinationItemDto()

        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        item.setOptions(itemCombinationOptions)
        item.setSequence(1)
        itemCombinationQuestionDto.getItemCombinationItems().add(item)
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)

        questionDto = questionService.createQuestion(courseExecution.getId(), questionDto)
    }


    def "teacher exports item combination questions"() {
        given: "a user"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: "a login"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export',
                requestContentType: 'application/json'
        )

        then: "check response status"
        assert response['response'].status == 200

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())
    }

    def "only teachers can export item combination questions"() {
        given:"a student"
        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export',
                requestContentType: 'application/json'
        )

        then: "check response status"
        assert response['response'].status == 403

        cleanup:
        userRepository.delete(userRepository.findById(student.getId()).get())
    }

    def "only logged teachers can export item combination questions"() {
        given: "a user"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export',
                requestContentType: 'application/json'
        )

        then: "check response status"
        assert response['response'].status == 403

        cleanup:
        userRepository.delete(userRepository.findById(teacher.getId()).get())

    }

    def "teachers without permission can't export item combination questions"() {
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

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export',
                requestContentType: 'application/json'
        )

        then: "check response status"
        assert response['response'].status == 403

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