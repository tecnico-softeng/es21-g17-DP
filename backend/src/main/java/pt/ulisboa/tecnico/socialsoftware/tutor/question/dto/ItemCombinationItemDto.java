package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationItemDto implements Serializable {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("sequence")
    private Integer sequence;

    @JsonProperty("options")
    private List<ItemCombinationOptionDto> options = new ArrayList<>();

    @JsonProperty("content")
    private String content;

    public ItemCombinationItemDto() {}

    public ItemCombinationItemDto(ItemCombinationItem item) {
        this.id = item.getId();
        this.sequence = item.getSequence();
        this.options = item.getOptions().stream().map(ItemCombinationOptionDto::new).collect(Collectors.toList());
        this.content = item.getContent();
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setOptions(List<ItemCombinationOptionDto> options) {
        this.options = options;
    }

    public List<ItemCombinationOptionDto> getOptions() {
        return this.options;
    }

    @Override
    public String toString()
    {
        return "ItemCombinationItemDto{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", options=" + options +
                ", content='" + content + '\'' +
                '}';
    }
}
