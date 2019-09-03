package eu.icole.portainer;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.spotify.docker.client.LogsResponseReader;
import com.spotify.docker.client.ObjectMapperProvider;
import com.spotify.docker.client.ProgressResponseReader;
import eu.icole.portainer.model.*;
import eu.icole.portainer.model.rest.*;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            ProgressResponseReader.class, MultiPartFeature.class);

    PortainerConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    void connect() throws PortainerException, NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslcontext = SSLContext.getInstance("TLS");

        sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new java.security.SecureRandom());

        Logger logger = Logger.getLogger(getClass().getName());

        Feature feature = new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, null);

        ClientBuilder cb = ClientBuilder.newBuilder().withConfig(defaultConfig);

        if (false) //TURN ON FOR DEBUG
            cb.register(feature).sslContext(sslcontext);

        Client client = cb.build();
        ;
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

    public static PortainerConnection connect(String url, String user, String password) throws PortainerException, KeyManagementException, NoSuchAlgorithmException {
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

    public WebTarget getRootWebTarget() {
        return webTarget;
    }

    public PortainerEndpoints getEndpoints() throws PortainerException {
        WebTarget authWebTarget
                = webTarget.path("endpoints");

        Invocation.Builder invocationBuilder
                = authWebTarget.request(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + getJwt());

        Response response
                = invocationBuilder.get();

        checkForError(response);

        List<RawEndpoint> list = response.readEntity(new GenericType<List<RawEndpoint>>() {
        });
        if (list.size() > 0)
            return new PortainerEndpoints(this, list);
        return null;
    }

    public PortainerEndpoint getEndpoint(String name) throws PortainerException {
        return getEndpoints().getEndpointByName(name);
    }


    public Stack createStack(StackDeployment stackDeployment) throws PortainerException, URISyntaxException {
        return createStack(stackDeployment, false);
    }

    public Stack createStack(StackDeployment stackDeployment, boolean force) throws PortainerException, URISyntaxException {

        Invocation.Builder invocationBuilder
                = getRootWebTarget().path("stacks")
                .queryParam("type", "" + stackDeployment.getType())
                .queryParam("method", stackDeployment.getMethod())
                .queryParam("endpointId", stackDeployment.getEndpointId() + "")
                .request(MediaType.APPLICATION_JSON_TYPE).
                        header("Authorization", "Bearer " + getJwt()).property("test", "test");

        Response response
                = invocationBuilder
                .post(Entity.entity(stackDeployment.getBody(), MediaType.APPLICATION_JSON_TYPE));

        if (response.getStatus() == 409 && force) {
            Stack stack = getStack(stackDeployment.getEndpointId(), stackDeployment.getBody().getName());
            if (stack == null)
                throw new PortainerException("Inconsistency detected. Cannot update stack " + stack.getName());
            return updateStack(stack, stackDeployment.getBody());
        } else
            checkForError(response);
        return response.readEntity(Stack.class);
    }

    public Stack getStack(int endpointId, String name) throws PortainerException {
        List<Stack> stackList = getStacks();
        for (Stack stack : stackList) {
            if (stack.getEndpointId() == endpointId && name.equals(stack.getName()))
                return stack;
        }
        return null;
    }

    public List<Stack> getStacks() throws PortainerException {
        Invocation.Builder invocationBuilder
                = getRootWebTarget().path("stacks").request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + getJwt());
        ;

        Response response = invocationBuilder.get();

        checkForError(response);

        return response.readEntity(new GenericType<List<Stack>>() {
        });
    }

    public Stack updateStack(Stack stack, StackDeploymentBody stackDeploymentBody) throws PortainerException {
        Invocation.Builder invocationBuilder
                = getRootWebTarget().path("stacks/" + stack.getId()).queryParam("endpointId", stack.getEndpointId()).
                request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + getJwt());
        Response response = invocationBuilder.put(Entity.entity(stackDeploymentBody, MediaType.APPLICATION_JSON_TYPE));
        checkForError(response);
        return response.readEntity(Stack.class);
    }


    public void deleteStack() {

    }
}
