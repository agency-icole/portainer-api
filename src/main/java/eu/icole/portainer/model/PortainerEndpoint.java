package eu.icole.portainer.model;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import eu.icole.portainer.PortainerConnection;
import eu.icole.portainer.PortainerDockerClient;
import eu.icole.portainer.model.rest.RawEndpoint;
import eu.icole.portainer.model.rest.StackDeployment;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;

public class PortainerEndpoint extends RawEndpoint {

    RawEndpoint endpoint;
    PortainerConnection connection;
    PortainerDockerClient dockerClient;
    public PortainerEndpoint(PortainerConnection connection, RawEndpoint rawEndpoint) {
        this.endpoint = rawEndpoint;
        this.connection = connection;
    }

    public int getId() {
        return endpoint.getId();
    }

    public String getName() {
        return endpoint.getName();
    }

    @Override
    public String toString() {
        return "PortainerEndpoint(id: " + getId() + " name: " + getName() + ")";
    }

    public synchronized DockerClient getDockerClient() {
        if(dockerClient==null) {
            try {
                dockerClient = new PortainerDockerClient(connection, this);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return dockerClient;
    }
}
