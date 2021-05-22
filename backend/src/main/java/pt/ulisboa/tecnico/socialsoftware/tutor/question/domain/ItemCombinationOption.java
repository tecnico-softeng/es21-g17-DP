package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto;

import javax.persistence.*;

@Entity
@Table(name = "item_combination_options")
public class ItemCombinationOption implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean correct;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean none;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_combination_id")
    private ItemCombinationItem itemCombinationItem;

    public ItemCombinationOption() {
    }

    public ItemCombinationOption(ItemCombinationOptionDto option) {
        setSequence(option.getSequence());
        setContent(option.getContent());
        setCorrect(option.isCorrect());
        setNone(option.isNone());
    }

    public Integer getId() {
        return id;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNone(boolean none) { this.none = none; }


    public ItemCombinationItem getItemCombinationItem() {
        return itemCombinationItem;
    }

    public Integer getSequence() {
        return sequence;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isNone() { return this.none; }

    public String getContent() {
        return content;
    }

    public void setItemCombinationItem(ItemCombinationItem itemCombinationItem) {
        this.itemCombinationItem = itemCombinationItem;
    }

    public void setItem(ItemCombinationItem item) {
        this.itemCombinationItem = item;
        item.addItemCombinationOption(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitItemCombinationOption(this);
    }

    public void delete() {
        this.itemCombinationItem = null;
    }
}
