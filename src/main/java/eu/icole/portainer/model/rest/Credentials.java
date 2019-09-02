package eu.icole.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credentials {
    String username;
    String password;

    public Credentials() {

    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @JsonProperty("Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
