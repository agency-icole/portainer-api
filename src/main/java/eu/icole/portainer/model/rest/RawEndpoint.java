package eu.icole.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawEndpoint {

    @JsonProperty("Id")
    long id;

    @JsonProperty("Name")
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
