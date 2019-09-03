package eu.icole.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawEndpoint {

    @JsonProperty("Id")
    int id;

    @JsonProperty("Name")
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
