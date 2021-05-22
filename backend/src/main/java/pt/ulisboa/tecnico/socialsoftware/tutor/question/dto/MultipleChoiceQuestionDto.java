package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();

    private boolean relevanceOfOptionsConsidered;

    public MultipleChoiceQuestionDto() {
    }

    public MultipleChoiceQuestionDto(MultipleChoiceQuestion question) {
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.relevanceOfOptionsConsidered = question.isRelevanceOfOptionsConsidered();
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public boolean isRelevanceOfOptionsConsidered() {
        return relevanceOfOptionsConsidered;
    }

    public void setRelevanceOfOptionsConsidered(boolean relevanceOfOptionsConsidered) {
        this.relevanceOfOptionsConsidered = relevanceOfOptionsConsidered;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new MultipleChoiceQuestion(question, this);
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                "relevanceOfOptionsConsidered=" + relevanceOfOptionsConsidered +
                '}';
    }

}
