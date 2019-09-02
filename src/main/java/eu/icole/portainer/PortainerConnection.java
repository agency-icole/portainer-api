package eu.icole.portainer;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.spotify.docker.client.LogsResponseReader;
import com.spotify.docker.client.ObjectMapperProvider;
import com.spotify.docker.client.ProgressResponseReader;
import eu.icole.portainer.model.*;
import eu.icole.portainer.model.rest.Authorization;
import eu.icole.portainer.model.rest.Credentials;
import eu.icole.portainer.model.rest.RawEndpoint;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class PortainerConnection {

    String url;
    String user;
    String password;

    String jwt;

    Client client;
    WebTarget webTarget;
    private final ClientConfig defaultConfig = new ClientConfig(
            ObjectMapperProvider.class,
            JacksonFeature.class,
            LogsResponseReader.class,
            ProgressResponseReader.class);

    PortainerConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    void connect() throws PortainerException {

        client = ClientBuilder.newClient(defaultConfig);
        JacksonJsonProvider jacksonJsonProvider =
                new JacksonJaxbJsonProvider()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client.register(jacksonJsonProvider);
        if (!url.endsWith("/"))
            url += "/";
        webTarget = client.target(url + "api");

        WebTarget authWebTarget
                = webTarget.path("auth");

        Invocation.Builder invocationBuilder
                = authWebTarget.request(MediaType.APPLICATION_JSON);

        Response response
                = invocationBuilder
                .post(Entity.entity(new Credentials(user, password), MediaType.APPLICATION_JSON));

        checkForError(response);

        Authorization authorization = response.readEntity(Authorization.class);
        this.jwt = authorization.getJwt();
    }

    public String getJwt() {
        return jwt;
    }

    public static PortainerConnection connect(String url, String user, String password) throws PortainerException {
        PortainerConnection connection = new PortainerConnection(url, user, password);
        connection.connect();
        return connection;
    }

    public static void checkForError(Response response) throws PortainerException {
        if (response.getStatus() != 200) {
            String message = "[" + response.getStatus() + "] " + response.getStatusInfo().getReasonPhrase();

            try {
                ErrorResponse err = response.readEntity(ErrorResponse.class);
                message += "\n" + err.toString();
            } catch (Exception e) {
            }

            throw new PortainerException(message);
        }
    }

    public WebTarget getRootWebTarget(){
        return webTarget;
    }

    public PortainerEndpoints getEndpoints() throws PortainerException {
        WebTarget authWebTarget
                = webTarget.path("endpoints");

        Invocation.Builder invocationBuilder
                = authWebTarget.request(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+getJwt());

        Response response
                = invocationBuilder.get();

         checkForError(response);

         List<RawEndpoint> list = response.readEntity(new GenericType<List<RawEndpoint>>(){});
         if(list.size()>0)
            return new PortainerEndpoints(this, list);
         return null;
    }

    public PortainerEndpoint getEndpoint(String name) throws PortainerException {
        return getEndpoints().getEndpointByName(name);
    }
}
