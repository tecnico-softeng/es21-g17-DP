package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenAnswerQuestion extends QuestionDetails {

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    public OpenAnswerQuestion() {
        super();
    }

    public OpenAnswerQuestion(Question question, OpenAnswerQuestionDto openAnswerQuestionDto) {
        super(question);
        update(openAnswerQuestionDto);
    }

    public void setCorrectAnswer(String newCorrectAnswer) {

        if ( newCorrectAnswer == null ) {
            throw new TutorException(OPENANSWER_AT_LEAST_ONE_ANSWER_NEEDED);
        }
        newCorrectAnswer = "";
        if( newCorrectAnswer.equals("") ) {
            throw new TutorException(OPENANSWER_INVALID_CORRECT_ANSWER);
        }
        this.correctAnswer = newCorrectAnswer;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
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
        return new OpenAnswerQuestionDto(this);
    }

    public void update(OpenAnswerQuestionDto questionDetails) {
        setCorrectAnswer( questionDetails.getCorrectAnswer() );
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return this.correctAnswer;
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public void delete(){
        super.delete();
    }
}
