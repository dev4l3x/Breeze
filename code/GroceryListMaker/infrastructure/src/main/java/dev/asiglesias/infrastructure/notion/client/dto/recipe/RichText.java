
package dev.asiglesias.infrastructure.notion.client.dto.recipe;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "plain_text"
})
@Generated("jsonschema2pojo")
public class RichText implements Serializable
{

    @JsonProperty("plain_text")
    private String plainText;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 8358015545603737873L;

    @JsonProperty("plain_text")
    public String getPlainText() {
        return plainText;
    }

    @JsonProperty("plain_text")
    public void setPlainText(String plainText) {
        this.plainText = plainText;
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
