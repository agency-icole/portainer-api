package eu.icole.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
@JsonInclude(JsonInclude.Include.NON_NULL)

public class StackDeploymentBody {

    String name;
    String swarmID;
    String stackFileContent;
    String repositoryURL;
    String repositoryReferenceName;
    String composeFilePathInRepository;
    boolean repositoryAuthentication;
    String repositoryUsername;
    String repositoryPassword;

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("SwarmID")
    public String getSwarmID() {
        return swarmID;
    }

    public void setSwarmID(String swarmID) {
        this.swarmID = swarmID;
    }

    @JsonProperty("StackFileContent")
    public String getStackFileContent() {
        return stackFileContent;
    }

    public void setStackFileContent(String stackFileContent) {
        this.stackFileContent = stackFileContent;
    }

    @JsonProperty("RepositoryURL")
    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }

    @JsonProperty("RepositoryReferenceName")
    public String getRepositoryReferenceName() {
        return repositoryReferenceName;
    }

    public void setRepositoryReferenceName(String repositoryReferenceName) {
        this.repositoryReferenceName = repositoryReferenceName;
    }

    @JsonProperty("ComposeFilePathInRepository")
    public String getComposeFilePathInRepository() {
        return composeFilePathInRepository;
    }

    public void setComposeFilePathInRepository(String composeFilePathInRepository) {
        this.composeFilePathInRepository = composeFilePathInRepository;
    }

    @JsonProperty("RepositoryAuthentication")
    public boolean isRepositoryAuthentication() {
        return repositoryAuthentication;
    }

    public void setRepositoryAuthentication(boolean repositoryAuthentication) {
        this.repositoryAuthentication = repositoryAuthentication;
    }

    @JsonProperty("RepositoryUsername")
    public String getRepositoryUsername() {
        return repositoryUsername;
    }

    public void setRepositoryUsername(String repositoryUsername) {
        this.repositoryUsername = repositoryUsername;
    }

    @JsonProperty("RepositoryPassword")
    public String getRepositoryPassword() {
        return repositoryPassword;
    }

    public void setRepositoryPassword(String repositoryPassword) {
        this.repositoryPassword = repositoryPassword;
    }
}
