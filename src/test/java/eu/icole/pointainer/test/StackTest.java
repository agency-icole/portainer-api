package eu.icole.pointainer.test;

import eu.icole.portainer.PortainerConnection;
import eu.icole.portainer.model.PortainerEndpoint;
import eu.icole.portainer.model.PortainerException;
import eu.icole.portainer.model.rest.StackDeployment;
import eu.icole.portainer.model.rest.StackDeploymentBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class StackTest {
    @BeforeEach
    public void setup(){
        PointainerTest.password = System.getProperty("portainer.password");
    }

    @Test
    public void testCreateStack() throws PortainerException, URISyntaxException {
        PortainerConnection connection = PortainerConnection.connect(PointainerTest.url,
                PointainerTest.username, PointainerTest.password);
        Assertions.assertNotNull(connection);

        PortainerEndpoint endpoint = connection.getEndpoint("local");
        Assertions.assertNotNull(endpoint);

        StackDeploymentBody body = new StackDeploymentBody();
        body.setName("test2"
        );
        body.setStackFileContent("version: '2'");
        StackDeployment deployment = new StackDeployment(StackDeployment.STHACK_TYPE_COMPOSE,
                StackDeployment.STACK_METHOD_STRING, endpoint.getId(), body);

        connection.createStack(deployment);
    }
}
