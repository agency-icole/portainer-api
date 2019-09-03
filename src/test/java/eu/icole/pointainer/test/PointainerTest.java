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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PointainerTest {

    public static String url = "http://docker.vpn.icole.eu:9000/";
    public static String username="monitor";
    public static String password;
    public static String endpoint="develop";

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
    public void testConnectionAndSuccess() throws PortainerException, NoSuchAlgorithmException, KeyManagementException {

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);
    }


    @Test
    public void testGetEndpoints() throws PortainerException, NoSuchAlgorithmException, KeyManagementException {

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        Assertions.assertNotNull(connection.getEndpoints());
    }

    @Test
    public void testGetLocalEndpoint() throws PortainerException, NoSuchAlgorithmException, KeyManagementException {

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint(PointainerTest.endpoint);
        Assertions.assertNotNull(endpoint);

    }


    @Test
    public void testGetContainers() throws PortainerException, NoSuchAlgorithmException, KeyManagementException {

        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint(PointainerTest.endpoint);
        Assertions.assertNotNull(endpoint);

        DockerClient client = endpoint.getDockerClient();
        Assertions.assertNotNull(client);
    }

    @Test
    public void testContainerList() throws PortainerException, DockerException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {
        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint(PointainerTest.endpoint);
        Assertions.assertNotNull(endpoint);

        DockerClient client = endpoint.getDockerClient();
        List<Container> containers = client.listContainers();
        Assertions.assertNotEquals(0,containers);
    }

    @Test
    public void testContainerInfo() throws PortainerException, DockerException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {
        PortainerConnection connection = PortainerConnection.connect(url, username, password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint(PointainerTest.endpoint);
        Assertions.assertNotNull(endpoint);

        DockerClient client = endpoint.getDockerClient();
        List<Container> containers = client.listContainers();
        Assertions.assertNotEquals(0,containers);

        Container container = containers.get(0);

        ContainerInfo info = endpoint.getDockerClient().inspectContainer(container.id());
        Assertions.assertNotNull(info);

    }

}
