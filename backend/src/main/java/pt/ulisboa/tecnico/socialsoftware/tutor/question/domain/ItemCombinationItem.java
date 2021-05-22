package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "item_combination_item")
public class ItemCombinationItem implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemCombinationItem", fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<ItemCombinationOption> options = new ArrayList<>();

    public ItemCombinationItem() {
    }

    public ItemCombinationItem(ItemCombinationItemDto itemCombinationItemDto) {
        setOptions(itemCombinationItemDto.getOptions());
        setSequence(itemCombinationItemDto.getSequence());
        setContent(itemCombinationItemDto.getContent());
    }

    public Integer getId() {
        return id;
    }

    public String getContent() { return ""; }

    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setContent(String content) { this.content = content; }

    public void setOptions(List<ItemCombinationOptionDto> options) {
        if (options.stream().filter(ItemCombinationOptionDto::isCorrect).count() == 0) {
            throw new TutorException(AT_LEAST_ONE_CORRECT_OPTION);
        }

        if(options.stream().filter(ItemCombinationOptionDto::isNone).count() != 1){
            throw new TutorException(ONLY_ONE_OPTION_NONE_ALLOWED);
        }

        if(options.stream().filter(ItemCombinationOptionDto::isCorrect).
                filter(ItemCombinationOptionDto::isNone).count() == 1 &&
        options.stream().filter(ItemCombinationOptionDto::isCorrect).count() > 1)
        {
            throw new TutorException(NONE_OPTION_IS_SET_CORRECT);
        }

        if(options.size() < 2)
            throw new TutorException(AT_LEAST_TWO_OPTION_NEEDED);

        this.options.clear();

        int index = 0;
        for (ItemCombinationOptionDto optionDto : options) {
            int newSequence = optionDto.getSequence() != null ? optionDto.getSequence() : index++;
            optionDto.setSequence(newSequence);
            ItemCombinationOption itemCombinationOption = new ItemCombinationOption(optionDto);
            itemCombinationOption.setItemCombinationItem(this);
            this.options.add(itemCombinationOption);
        }
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setQuestionDetails(ItemCombinationQuestion question) {
        this.questionDetails = question;
    }

    public void addItemCombinationOption(ItemCombinationOption option)
    {
        this.options.add(option);
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitItemCombinationItem(this);
    }

    public List<ItemCombinationOption> getOptions() {
        return options;
    }

    public void visitOptions(Visitor visitor) {
        for (ItemCombinationOption option : this.getOptions()) {
            option.accept(visitor);
        }
    }

    public void delete() {
        this.questionDetails = null;
        for (ItemCombinationOption option : this.options) {
            option.delete();
        }
        this.options.clear();
    }
}
