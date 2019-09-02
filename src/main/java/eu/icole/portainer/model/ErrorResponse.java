package eu.icole.portainer.model;

public class ErrorResponse {

    String err;
    String details;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String toString(){
        return err+" : "+details;
    }
}
