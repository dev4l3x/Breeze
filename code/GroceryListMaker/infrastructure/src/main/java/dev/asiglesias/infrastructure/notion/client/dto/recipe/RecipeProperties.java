
package dev.asiglesias.infrastructure.notion.client.dto.recipe;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Ingredients"
})
@Generated("jsonschema2pojo")
public class RecipeProperties implements Serializable
{

    @JsonProperty("Ingredients")
    private Ingredients ingredients;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -8264812873927774646L;

    @JsonProperty("Ingredients")
    public Ingredients getIngredients() {
        return ingredients;
    }

    @JsonProperty("Ingredients")
    public void setIngredients(Ingredients ingredients) {
        this.ingredients = ingredients;
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
