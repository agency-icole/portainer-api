package eu.icole.portainer.model.rest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.core.MediaType;
import java.awt.*;

public class StackDeployment {

    public static final int STACK_TYPE_SWARM = 1;
    public static final int STHACK_TYPE_COMPOSE = 2;

    public static final String STACK_METHOD_STRING = "string";
    public static final String STACK_METHOD_REPOSITORY = "repository";

    int endpointId;
    int type;
    String method;

    StackDeploymentBody body;

    public StackDeployment(int type, String method, int endpointID, StackDeploymentBody body){
        this.body = body;
        this.type=type;
        this.method=method;
        this.endpointId=endpointID;
    }

    public int getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(int endpointId) {
        this.endpointId = endpointId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public StackDeploymentBody getBody() {
        return body;
    }

    public void setBody(StackDeploymentBody body) {
        this.body = body;
    }
}
