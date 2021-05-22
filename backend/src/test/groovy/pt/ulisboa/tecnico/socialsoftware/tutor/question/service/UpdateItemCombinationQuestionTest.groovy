package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationItem
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class UpdateItemCombinationQuestionTest extends SpockTest {

    def question
    def option1
    def option2

    def setup() {
        def image = new Image()
        def questionDetails = new ItemCombinationQuestion()
        def itemCombinationItem1 = new ItemCombinationItem()
        def itemCombinationItem2 = new ItemCombinationItem()


        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)

        question.setCourse(externalCourse)
        question.setImage(image)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        option1 = new ItemCombinationOption()
        option1.setContent(OPTION_1_CONTENT)
        option1.setCorrect(true)
        option1.setSequence(1)
        option1.setNone(true)
        option1.setItem(itemCombinationItem1)
        option2 = new ItemCombinationOption()
        option2.setContent(OPTION_1_CONTENT)
        option2.setCorrect(false)
        option2.setNone(false)
        option2.setSequence(2)
        option2.setItem(itemCombinationItem2)
        itemCombinationOptionRepository.save(option1)
        itemCombinationOptionRepository.save(option2)

        itemCombinationItem1.setSequence(1)
        itemCombinationItem2.setSequence(2)
        itemCombinationItem1.setQuestionDetails(questionDetails)
        itemCombinationItem2.setQuestionDetails(questionDetails)
        itemCombinationItemRepository.save(itemCombinationItem1)
        itemCombinationItemRepository.save(itemCombinationItem2)
    }

    def "can't update a question with no correct options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: "an optionDto"
        def itemCombinationOptionDto1 = new ItemCombinationOptionDto()
        def itemCombinationOptionDto2 = new ItemCombinationOptionDto()
        def itemCombinationOptions = new ArrayList<ItemCombinationOptionDto>()
        itemCombinationOptionDto1.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto1.setCorrect(false)
        itemCombinationOptionDto2.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto2.setCorrect(false)
        itemCombinationOptions.add(itemCombinationOptionDto1)
        itemCombinationOptions.add(itemCombinationOptionDto2)

        and: "an itemCombinationItemDto"
        def item = new ItemCombinationItemDto()
        item.setOptions(itemCombinationOptions)

        and: "an itemCombinationQuestionDto"
        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        itemCombinationQuestionDto.getItemCombinationItems().add(item)
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_CORRECT_OPTION

    }

    def "can't update a question with less than 2 options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: "an optionDto"
        def itemCombinationOptionDto = new ItemCombinationOptionDto()
        def itemCombinationOptions = new ArrayList<ItemCombinationOptionDto>()

        itemCombinationOptionDto.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto.setCorrect(true)
        itemCombinationOptionDto.setNone(true)
        itemCombinationOptions.add(itemCombinationOptionDto)

        and: "an itemCombinationQuestionDto"
        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        and: "an itemCombinationItemDto"
        def item = new ItemCombinationItemDto()

        item.setOptions(itemCombinationOptions)
        item.setSequence(1)
        itemCombinationQuestionDto.getItemCombinationItems().add(item)


        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_OPTION_NEEDED
    }

    def "can't update to more option correct, if that option has none flag set to true"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: "an optionDto"
        def itemCombinationOptionDto = new ItemCombinationOptionDto()
        def itemCombinationOptionDto2 = new ItemCombinationOptionDto()
        def itemCombinationOptions = new ArrayList<ItemCombinationOptionDto>()

        itemCombinationOptionDto.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto.setCorrect(true)
        itemCombinationOptionDto.setNone(true)
        itemCombinationOptionDto2.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto2.setCorrect(true)
        itemCombinationOptionDto2.setNone(false)
        itemCombinationOptions.add(itemCombinationOptionDto)
        itemCombinationOptions.add(itemCombinationOptionDto2)

        and: "an itemCombinationQuestionDto"
        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        and: "an itemCombinationItemDto"
        def item = new ItemCombinationItemDto()

        item.setOptions(itemCombinationOptions)
        item.setSequence(1)
        itemCombinationQuestionDto.getItemCombinationItems().add(item)
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NONE_OPTION_IS_SET_CORRECT
    }

    def "can't update a question having more than one option with none flag set to true"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: "an optionDto"
        def itemCombinationOptionDto = new ItemCombinationOptionDto()
        def itemCombinationOptionDto2 = new ItemCombinationOptionDto()
        def itemCombinationOptions = new ArrayList<ItemCombinationOptionDto>()

        itemCombinationOptionDto.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto.setCorrect(true)
        itemCombinationOptionDto.setNone(true)
        itemCombinationOptionDto2.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto2.setCorrect(false)
        itemCombinationOptionDto2.setNone(true)
        itemCombinationOptions.add(itemCombinationOptionDto)
        itemCombinationOptions.add(itemCombinationOptionDto2)

        and: "an itemCombinationQuestionDto"
        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        and: "an itemCombinationItemDto"
        def item = new ItemCombinationItemDto()

        item.setOptions(itemCombinationOptions)
        item.setSequence(1)
        itemCombinationQuestionDto.getItemCombinationItems().add(item)
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ONLY_ONE_OPTION_NONE_ALLOWED
    }

    def "can't update a question with no items"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: "an itemCombinationQuestionDto"
        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_ITEM_NEEDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}