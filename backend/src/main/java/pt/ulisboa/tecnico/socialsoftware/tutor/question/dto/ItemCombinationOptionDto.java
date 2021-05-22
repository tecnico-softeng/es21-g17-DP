package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;
import java.io.Serializable;

public class ItemCombinationOptionDto implements Serializable {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("sequence")
    private Integer sequence;

    @JsonProperty("correct")
    private boolean correct;

    @JsonProperty("content")
    private String content;

    @JsonProperty("none")
    private boolean none;

    public ItemCombinationOptionDto() {
    }

    public ItemCombinationOptionDto(ItemCombinationOption itemCombinationOption) {
        this.id = itemCombinationOption.getId();
        this.sequence = itemCombinationOption.getSequence();
        this.content = itemCombinationOption.getContent();
        this.correct = itemCombinationOption.isCorrect();
        this.none = itemCombinationOption.isNone();
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isNone() { return this.none; }

    public void setNone(boolean none) { this.none = none; }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ItemCombinationOptionDto{" +
                "id=" + id +
                ", sequence" +
                ", content='" + content + '\'' +
                ", correct=" + correct +
                ", none=" + none +
                '}';
    }
}