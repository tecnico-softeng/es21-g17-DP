package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<ItemCombinationItem> itemCombinationItems = new ArrayList<>();

    public ItemCombinationQuestion() { super(); }

    public ItemCombinationQuestion(Question question, ItemCombinationQuestionDto itemCombinationQuestionDto) {
        super(question);
        update(itemCombinationQuestionDto);
    }

    public void setItemCombinationItems(List<ItemCombinationItemDto> itemCombinationItemDto) {
        if (itemCombinationItemDto.isEmpty()) {
            throw new TutorException(AT_LEAST_ONE_ITEM_NEEDED);
        }

        itemCombinationItems.clear();

        for (ItemCombinationItemDto newItemCombinationItemDto : itemCombinationItemDto) {
            ItemCombinationItem itemCombinationItem = new ItemCombinationItem(newItemCombinationItemDto);
            itemCombinationItem.setQuestionDetails(this);
            this.itemCombinationItems.add(itemCombinationItem);
        }
    }

    public List<ItemCombinationItem> getItemCombinationItems() { return itemCombinationItems; }


    public ItemCombinationItem getItemCombinationItemById(Integer itemId) {
        return this.itemCombinationItems
                .stream()
                .filter(item1 -> item1.getId().equals(itemId))
                .findAny()
                .orElseThrow(() -> new TutorException(QUESTION_ITEM_MISMATCH, itemId));
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null;
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null;
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new ItemCombinationQuestionDto(this);
    }

    public void update(ItemCombinationQuestionDto questionDetails) {
        setItemCombinationItems(questionDetails.getItemCombinationItems());
    }

    public void addItem(ItemCombinationItem item)
    {
        this.itemCombinationItems.add(item);
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return null;
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitItemCombinationItems(Visitor visitor) {
        for (var item : this.getItemCombinationItems()) {
            item.accept(visitor);
        }
    }

    @Override
    public void delete(){
        super.delete();
        for(var item: this.itemCombinationItems)
            item.delete();
        this.itemCombinationItems.clear();
    }
}
