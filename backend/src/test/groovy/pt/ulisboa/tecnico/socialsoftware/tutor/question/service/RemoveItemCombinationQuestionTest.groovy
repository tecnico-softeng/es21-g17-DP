package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationItem
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class RemoveItemCombinationQuestion extends SpockTest {

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

    def "remove an item question"() {
        when:
        questionService.removeQuestion(question.getId())

        then: "the question is removed"
        questionRepository.count() == 0L
        imageRepository.count() == 0L
        itemCombinationOptionRepository.count() == 0L
        itemCombinationItemRepository.count() == 0L

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
