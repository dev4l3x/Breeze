
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

    @JsonProperty("Dinner Quantity")
    private Quantity dinnerQuantity;
    @JsonProperty("Dinner")
    private Meal dinner;
    @JsonProperty("Lunch Quantity")
    private Quantity lunchQuantity;
    @JsonProperty("Lunch")
    private Meal lunch;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -6033746548213943504L;

    @JsonProperty("Dinner Quantity")
    public Quantity getDinnerQuantity() {
        return dinnerQuantity;
    }

    @JsonProperty("Dinner Quantity")
    public void setDinnerQuantity(Quantity dinnerQuantity) {
        this.dinnerQuantity = dinnerQuantity;
    }

    @JsonProperty("Dinner")
    public Meal getDinner() {
        return dinner;
    }

    @JsonProperty("Dinner")
    public void setDinner(Meal dinner) {
        this.dinner = dinner;
    }

    @JsonProperty("Lunch Quantity")
    public Quantity getLunchQuantity() {
        return lunchQuantity;
    }

    @JsonProperty("Lunch Quantity")
    public void setLunchQuantity(Quantity lunchQuantity) {
        this.lunchQuantity = lunchQuantity;
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
