package eu.icole.portainer;

import com.google.common.collect.Lists;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.ObjectMapperProvider;
import com.spotify.docker.client.exceptions.DockerException;
import eu.icole.portainer.model.PortainerEndpoint;

import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortainerDockerClient extends DefaultDockerClient {

    PortainerConnection connection;
    public PortainerDockerClient(PortainerConnection connection, PortainerEndpoint endpoint) throws URISyntaxException {

        super(new PortainerBuilder(connection, endpoint));
        this.connection = connection;
    }

    private WebTarget addParameters(WebTarget resource, final Param... params)
            throws DockerException {
        
        final Map<String, List<String>> filters = new HashMap<>();
        for (final Param param : params) {
            if (param instanceof FilterParam) {
                List<String> filterValueList;
                if (filters.containsKey(param.name())) {
                    filterValueList = filters.get(param.name());
                } else {
                    filterValueList = Lists.newArrayList();
                }
                filterValueList.add(param.value());
                filters.put(param.name(), filterValueList);
            } else {
                resource = resource.queryParam(urlEncode(param.name()), urlEncode(param.value()));
            }
        }

        if (!filters.isEmpty()) {
            // If filters were specified, we must put them in a JSON object and pass them using the
            // 'filters' query param like this: filters={"dangling":["true"]}. If filters is an empty map,
            // urlEncodeFilters will return null and queryParam() will remove that query parameter.
            resource = resource.queryParam("filters", urlEncodeFilters(filters));
        }
        return resource;
    }

    /**
     * URL-encodes a string when used as a URL query parameter's value.
     *
     * @param unencoded A string that may contain characters not allowed in URL query parameters.
     * @return URL-encoded String
     * @throws DockerException if there's an UnsupportedEncodingException
     */
    private String urlEncode(final String unencoded) throws DockerException {
        try {
            final String encode = URLEncoder.encode(unencoded, "UTF-8");
            return encode.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new DockerException(e);
        }
    }

    /**
     * Takes a map of filters and URL-encodes them. If the map is empty or an exception occurs, return
     * null.
     *
     * @param filters A map of filters.
     * @return String
     * @throws DockerException if there's an IOException
     */
    private String urlEncodeFilters(final Map<String, List<String>> filters) throws DockerException {
        try {
            final String unencodedFilters = ObjectMapperProvider.objectMapper().writeValueAsString(filters);
            if (!unencodedFilters.isEmpty()) {
                return urlEncode(unencodedFilters);
            }
        } catch (IOException e) {
            throw new DockerException(e);
        }
        return null;
    }
    public static class PortainerBuilder extends DefaultDockerClient.Builder{
        public PortainerBuilder(PortainerConnection connection, PortainerEndpoint endpoint) throws URISyntaxException {
            super();
            this.uri(new URI(connection.webTarget.getUri()+"/endpoints/"+endpoint.getId()+"/docker/"));
            super.header("Authorization", "Bearer "+connection.getJwt());
        }

    }

}

