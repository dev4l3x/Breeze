
package dev.asiglesias.infrastructure.notion.client.dto;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Dinner Quantity",
    "Dinner",
    "Lunch Quantity",
    "Lunch"
})
@Generated("jsonschema2pojo")
public class Properties implements Serializable
{

    @JsonProperty("Dinner Servings")
    private Quantity dinnerServings;
    @JsonProperty("Dinner")
    private Meal dinner;
    @JsonProperty("Lunch Servings")
    private Quantity lunchServings;
    @JsonProperty("Lunch")
    private Meal lunch;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -6033746548213943504L;

    @JsonProperty("Dinner Servings")
    public Quantity getDinnerServings() {
        return dinnerServings;
    }

    @JsonProperty("Dinner Servings")
    public void setDinnerServings(Quantity dinnerServings) {
        this.dinnerServings = dinnerServings;
    }

    @JsonProperty("Dinner")
    public Meal getDinner() {
        return dinner;
    }

    @JsonProperty("Dinner")
    public void setDinner(Meal dinner) {
        this.dinner = dinner;
    }

    @JsonProperty("Lunch Servings")
    public Quantity getLunchServings() {
        return lunchServings;
    }

    @JsonProperty("Lunch Servings")
    public void setLunchServings(Quantity lunchServings) {
        this.lunchServings = lunchServings;
    }

    @JsonProperty("Lunch")
    public Meal getLunch() {
        return lunch;
    }

    @JsonProperty("Lunch")
    public void setLunch(Meal lunch) {
        this.lunch = lunch;
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
