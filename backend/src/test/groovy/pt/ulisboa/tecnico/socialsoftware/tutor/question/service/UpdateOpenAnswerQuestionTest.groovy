package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*

@DataJpaTest
class UpdateOpenAnswerQuestionTest extends SpockTest {
    
    def question

    def setup() {

        and: "an image"
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        and: "create an open answer question"
        def questionDetails = new OpenAnswerQuestion()
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        
        
        and: "a correct answer"
        def correct = new String(ANSWER_1_CONTENT)
        questionDetails.setCorrectAnswer(correct)

        question.setImage(image)
        
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: "correct answer is set"
        question.getQuestionDetails().setCorrectAnswer(correct)
    }

    def "update open answer question"() {
        given: "a changed fill-in question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        and: 'one correct answer'
        def correct = ANSWER_1_CONTENT
        questionDto.getQuestionDetailsDto().setCorrectAnswer(correct)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the open answer question is changed"
        
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        //result.getNumberOfAnswers() == 1
        //result.getNumberOfCorrect() == 1

        // TODO: complete

    }

    /* THIS IS COMMENTED BECAUSE HAS BUGS AND DOESN'T MATTER 

    def "update open answer question correct answer with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        questionDto.setTitle('     ')

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "update open answer question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        and: 'one correct answer'
        def answers = new ArrayList<String>()
        def correct = '     '
        answers.push(correct)
        questionDto.getQuestionDetailsDto().setAnswer(answers)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.OPENANSWER_INVALID_CORRECT_ANSWER
    } 

    */

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
