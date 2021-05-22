package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question



@DataJpaTest
class ImportExportItemCombinationQuestionsTest extends SpockTest {

    Integer questionId

    def setup() {
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def itemCombinationOptionDto1 = new ItemCombinationOptionDto()
        def itemCombinationOptionDto2 = new ItemCombinationOptionDto()
        def options = new ArrayList<ItemCombinationOptionDto>()
        def image = new ImageDto()

        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)
        itemCombinationOptionDto1.setContent(OPTION_1_CONTENT)
        itemCombinationOptionDto1.setCorrect(true)
        itemCombinationOptionDto1.setNone(true)
        itemCombinationOptionDto2.setContent(OPTION_2_CONTENT)
        itemCombinationOptionDto2.setCorrect(false)
        itemCombinationOptionDto2.setNone(false)
        options.add(itemCombinationOptionDto1)
        options.add(itemCombinationOptionDto2)


        def item = new ItemCombinationItemDto()
        def item2 = new ItemCombinationItemDto()
        def itemCombinationQuestionDto = new ItemCombinationQuestionDto()

        item.setOptions(options)
        item.setSequence(1)
        item2.setOptions(options)
        item2.setSequence(2)
        itemCombinationQuestionDto.getItemCombinationItems().add(item)
        itemCombinationQuestionDto.getItemCombinationItems().add(item2)
        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)
        questionId = questionService.createQuestion(externalCourse.getId(), questionDto).getId()

    }

    def 'export and import questions to xml'() {

        given: 'a xml with questions'
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        and: 'a clean database'
        questionService.removeQuestion(questionId)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def questionResult = questionService.findQuestions(externalCourse.getId()).get(0)
        questionResult.getKey() == null
        questionResult.getTitle() == QUESTION_1_TITLE
        questionResult.getContent() == QUESTION_1_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE.name()
        def imageResult = questionResult.getImage()
        imageResult.getWidth() == 20
        imageResult.getUrl() == IMAGE_1_URL

        def itemCombinationQuestionDto = (ItemCombinationQuestionDto) questionResult.getQuestionDetailsDto()
        itemCombinationQuestionDto.getItemCombinationItems().size() == 2
        def resOption =  (List<ItemCombinationOptionDto>) itemCombinationQuestionDto.getItemCombinationItems().get(0).getOptions()
        def resOption2 = (List<ItemCombinationOptionDto>) itemCombinationQuestionDto.getItemCombinationItems().get(1).getOptions()

        resOption.get(0).getContent() == OPTION_1_CONTENT
        resOption.get(1).getContent() == OPTION_2_CONTENT
        resOption.get(0).isCorrect()
        !resOption.get(1).isCorrect()
        resOption.get(0).isNone()
        !resOption.get(1).isNone()

        resOption2.get(0).getContent() == OPTION_1_CONTENT
        resOption2.get(1).getContent() == OPTION_2_CONTENT
        resOption2.get(0).isCorrect()
        !resOption2.get(1).isCorrect()
        resOption2.get(0).isNone()
        !resOption2.get(1).isNone()



    }

    def 'export to latex'() {
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()
        print questionsLatex

        then:
        questionsLatex != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
