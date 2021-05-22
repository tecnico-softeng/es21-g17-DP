package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;

public class OptionDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private boolean correct;
    private Integer relevance;
    private String content;

    public OptionDto() {
    }

    public OptionDto(Option option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
        this.relevance = option.getRelevance();
    }

    public OptionDto(CodeFillInOption option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
    }

    public OptionDto(ItemCombinationOption option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
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

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Integer getRelevance() {
        return relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "OptionDto{" +
                "id=" + id +
                ", correct=" + correct +
                ", relevance=" + relevance +
                ", content='" + content + '\'' +
                '}';
    }
}