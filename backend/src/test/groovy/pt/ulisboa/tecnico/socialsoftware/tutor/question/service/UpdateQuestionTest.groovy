package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class UpdateQuestionTest extends SpockTest {
    def question
    def optionOK
    def optionKO
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setImage(image)
        def questionDetails = new MultipleChoiceQuestion()
        questionDetails.setRelevanceOfOptionsConsidered(true)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
    }

    def "update a question"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '2 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();
        questionRepository.count();

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT

        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() != null

        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().get(0)
        resOptionOne.getContent() == OPTION_2_CONTENT
        !resOptionOne.isCorrect()
        def resOptionTwo = result.getQuestionDetails().getOptions().get(1)
        resOptionTwo.getContent() == OPTION_2_CONTENT
        resOptionTwo.isCorrect()
    }

    def "update question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setTitle('     ')

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }


    def "update question with two options true"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK)
        options.add(optionDto)

        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setRelevanceOfOptionsConsidered(true)

        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is not changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getDifficulty() == 50
        result.getImage() != null
        result.getQuestionDetails().isRelevanceOfOptionsConsidered() == true

        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().get(0)
        resOptionOne.getContent() == OPTION_1_CONTENT
        resOptionOne.isCorrect()
        def resOptionTwo = result.getQuestionDetails().getOptions().get(1)
        resOptionTwo.getContent() == OPTION_2_CONTENT
        resOptionTwo.isCorrect()
    }

    def "update a multiple choice question with 2 options to not have an order of relevance anymore"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        def multipleChoiceQuestionDto = new MultipleChoiceQuestionDto()
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        multipleChoiceQuestionDto.setRelevanceOfOptionsConsidered(false)
        questionDto.setQuestionDetailsDto(multipleChoiceQuestionDto)

        def options = new ArrayList<OptionDto>()

        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)

        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)

        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();

        when:
        def test= questionService.updateQuestion(question.getId(), questionDto)

        then:
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        result.getQuestionDetails().getOptions().size() == 2
        !result.getQuestionDetailsDto().isRelevanceOfOptionsConsidered()
        def optionOneResult = result.getQuestionDetailsDto().getOptions().get(0)
        def optionTwoResult = result.getQuestionDetailsDto().getOptions().get(1)
        optionOneResult.isCorrect()
        !optionTwoResult.isCorrect()
        externalCourse.getQuestions().contains(result)

    }

    def "cannot update a multiple choice question to have no correct options"() {
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_CORRECT_OPTION_NEEDED
    }

    def "update correct option in a question with answers"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        questionAnswer = new QuestionAnswer()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)


        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'a optionId'
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CHANGE_ANSWERED_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
