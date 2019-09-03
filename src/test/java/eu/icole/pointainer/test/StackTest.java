package eu.icole.pointainer.test;

import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.Swarm;
import eu.icole.portainer.PortainerConnection;
import eu.icole.portainer.model.PortainerEndpoint;
import eu.icole.portainer.model.PortainerException;
import eu.icole.portainer.model.rest.Stack;
import eu.icole.portainer.model.rest.StackDeployment;
import eu.icole.portainer.model.rest.StackDeploymentBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class StackTest {
    @BeforeEach
    public void setup(){
        PointainerTest.password = System.getProperty("portainer.password");
    }

    @Test
    public void testCreateStack() throws PortainerException, URISyntaxException, NoSuchAlgorithmException,
            KeyManagementException, DockerException, InterruptedException {
        PortainerConnection connection = PortainerConnection.connect(PointainerTest.url,
                PointainerTest.username, PointainerTest.password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint("develop");
        Assertions.assertNotNull(endpoint);

        Swarm swarm = endpoint.getDockerClient().inspectSwarm();
        Assertions.assertNotNull(swarm);
        StackDeploymentBody body = new StackDeploymentBody();

        String stackName = "junit-test";

        body.setName(stackName);
        body.setStackFileContent("version: '3'");
        body.setSwarmID(swarm.id());
        StackDeployment deployment = new StackDeployment(StackDeployment.STACK_TYPE_SWARM,
                StackDeployment.STACK_METHOD_STRING, endpoint.getId(), body);

        Stack stack = connection.createStack(deployment,true);
        Assertions.assertNotNull(stack);
        Assertions.assertEquals(stackName,stack.getName());
        Assertions.assertNotNull(stack.getId());
    }
}
