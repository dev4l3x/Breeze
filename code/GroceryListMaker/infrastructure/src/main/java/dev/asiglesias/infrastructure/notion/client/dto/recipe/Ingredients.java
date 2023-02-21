
package dev.asiglesias.infrastructure.notion.client.dto.recipe;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "rich_text"
})
@Generated("jsonschema2pojo")
public class Ingredients implements Serializable
{

    @JsonProperty("rich_text")
    private List<RichText> richText;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 9184342863925077138L;

    @JsonProperty("rich_text")
    public List<RichText> getRichText() {
        return richText;
    }

    @JsonProperty("rich_text")
    public void setRichText(List<RichText> richText) {
        this.richText = richText;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
