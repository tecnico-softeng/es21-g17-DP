package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

import java.util.List;

public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerDetailsDto {

    private List<Integer> correctOptionsIds;

    private boolean relevanceOfOptionsConsidered;

    public MultipleChoiceCorrectAnswerDto(MultipleChoiceQuestion question) {
        this.correctOptionsIds = question.getCorrectOptionsIds();
        this.relevanceOfOptionsConsidered = question.isRelevanceOfOptionsConsidered();
    }

    public List<Integer> getCorrectOptionsIds() {
        return correctOptionsIds;
    }

    public void setCorrectOptionsIds(List<Integer> correctOptionsIds) {
        this.correctOptionsIds = correctOptionsIds;
    }

    public boolean isRelevanceOfOptionsConsidered() {
        return relevanceOfOptionsConsidered;
    }

    public void setRelevanceOfOptionsConsidered(boolean relevanceOfOptionsConsidered) {
        this.relevanceOfOptionsConsidered = relevanceOfOptionsConsidered;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("MultipleChoiceCorrectAnswerDto{");
        boolean isTheFirstId = true;

        for (Integer id : this.correctOptionsIds) {

            if (isTheFirstId) {
                string.append("correctOptionId=").append(id);
                isTheFirstId = false;
            }
            else
                string.append(", correctOptionId=").append(id);
        }

        return string.append('}').toString();
    }
}