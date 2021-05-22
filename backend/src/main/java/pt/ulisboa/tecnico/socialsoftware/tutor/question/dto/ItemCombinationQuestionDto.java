package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationQuestionDto extends QuestionDetailsDto {

    @JsonProperty("items")
    private List<ItemCombinationItemDto> items = new ArrayList<>();

    public ItemCombinationQuestionDto() {
    }
    
    public ItemCombinationQuestionDto(ItemCombinationQuestion question) {
        this.items = question.getItemCombinationItems().stream().map(ItemCombinationItemDto::new).collect(Collectors.toList());
    }

    public void setItemCombinationItems(List<ItemCombinationItemDto> items) {
        this.items = items;
    }

    public List<ItemCombinationItemDto> getItemCombinationItems() {
        return this.items;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {

        return new ItemCombinationQuestion(question, this);
    }

    @Override
    public void update(ItemCombinationQuestion question) {
        question.update(this);
    }

    @Override
    public String toString()
    {
        return "ItemCombinationQuestionDto{" +
                "items=" + items +
                '}';
    }
}