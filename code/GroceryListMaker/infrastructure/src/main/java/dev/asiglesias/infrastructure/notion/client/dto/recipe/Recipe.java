
package dev.asiglesias.infrastructure.notion.client.dto.recipe;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "properties"
})
@Generated("jsonschema2pojo")
public class Recipe implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("properties")
    private RecipeProperties properties;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 8274436349990542066L;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("properties")
    public RecipeProperties getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(RecipeProperties properties) {
        this.properties = properties;
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
