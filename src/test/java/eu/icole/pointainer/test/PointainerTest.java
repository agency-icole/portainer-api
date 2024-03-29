package eu.icole.pointainer.test;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import eu.icole.portainer.PortainerConnection;
import eu.icole.portainer.model.DockerContainer;
import eu.icole.portainer.model.PortainerEndpoint;
import eu.icole.portainer.model.rest.RawEndpoint;
import eu.icole.portainer.model.PortainerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

public class PointainerTest {

    String url = "http://docker.vpn.icole.eu:9000/";
    String username="monitor";
    String password;

    String local = "local";

    @BeforeEach
    public void setup(){
        password = System.getProperty("portainer.password");
    }

    @Test
    public void testConnectionWithWrongCredentailsAndFail(){
        Assertions.assertThrows(PortainerException.class, ()-> {
                PortainerConnection connection = PortainerConnection.connect(url, username, "wrong");
        });

    }

    @Test
    public void testConnectionAndSuccess() throws PortainerException{

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);
    }


    @Test
    public void testGetEndpoints() throws PortainerException{

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        Assertions.assertNotNull(connection.getEndpoints());
    }

    @Test
    public void testGetLocalEndpoint() throws PortainerException{

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint("local");
        Assertions.assertNotNull(endpoint);

    }


    @Test
    public void testGetContainers() throws PortainerException {

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint("local");
        Assertions.assertNotNull(endpoint);

        DockerClient client = endpoint.getDockerClient();
        Assertions.assertNotNull(client);
    }

    @Test
    public void testContainerList() throws PortainerException, DockerException, InterruptedException {
        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint("local");
        Assertions.assertNotNull(endpoint);

        DockerClient client = endpoint.getDockerClient();
        List<Container> containers = client.listContainers();
        Assertions.assertNotEquals(0,containers);
    }

    @Test
    public void testContainerInfo() throws PortainerException, DockerException, InterruptedException{
        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint("local");
        Assertions.assertNotNull(endpoint);

        DockerClient client = endpoint.getDockerClient();
        List<Container> containers = client.listContainers();
        Assertions.assertNotEquals(0,containers);

        Container container = containers.get(0);

        ContainerInfo info = endpoint.getDockerClient().inspectContainer(container.id());
        Assertions.assertNotNull(info);

    }

}
