package eu.icole.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stack {

    String name;
    String id;
    int endpointId;

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("EndpointId")
    public int getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(int endpointId) {
        this.endpointId = endpointId;
    }
}
