
package dev.asiglesias.infrastructure.notion.client.dto;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "relation"
})
@Generated("jsonschema2pojo")
public class Meal implements Serializable
{

    @JsonProperty("relation")
    private List<Relation> relation;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -4156405221498096973L;

    @JsonProperty("relation")
    public List<Relation> getRelation() {
        return relation;
    }

    @JsonProperty("relation")
    public void setRelation(List<Relation> relation) {
        this.relation = relation;
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
