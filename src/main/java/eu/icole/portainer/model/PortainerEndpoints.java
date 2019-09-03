package eu.icole.portainer.model;

import eu.icole.portainer.PortainerConnection;
import eu.icole.portainer.model.rest.RawEndpoint;

import javax.xml.ws.Endpoint;
import java.util.LinkedList;
import java.util.List;

public class PortainerEndpoints {

    List<PortainerEndpoint> endpoints;
    PortainerConnection connection;

    public PortainerEndpoints(PortainerConnection connection, List<RawEndpoint> list){
        endpoints = new LinkedList<>();
        for(RawEndpoint rawEndpoint: list){
            endpoints.add(new PortainerEndpoint(connection, rawEndpoint));
        }
    }

    public List<PortainerEndpoint> getEndpoints(){
        return endpoints;
    }
    public PortainerEndpoint getEndpointByName(String name){

        for(PortainerEndpoint endpoint : endpoints){
            if(endpoint.getName().equals(name))
                return endpoint;
        }
        return null;
    }

    public List<PortainerEndpoint> getRawEndpoints(){
        return endpoints;
    }

    @Override
    public String toString() {
        return endpoints.toString();
    }
}
