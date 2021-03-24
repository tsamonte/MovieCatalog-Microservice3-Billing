package edu.uci.ics.tsamonte.service.billing.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.logger.ServiceLogger;

import javax.ws.rs.core.Response;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {
    @JsonIgnore
    private Result result;

    public ResponseModel() { }

    public ResponseModel(Result result)
    {
        this.result = result;
    }

    @JsonProperty("resultCode")
    public int getResultCode()
    {
        return result.getResultCode();
    }

    @JsonProperty("message")
    public String getMessage()
    {
        return result.getMessage();
    }

    @JsonIgnore
    public Result getResult()
    {
        return result;
    }

    @JsonIgnore
    public void setResult(Result result)
    {
        this.result = result;
    }

    @JsonIgnore
    public Response buildResponse()
    {
        ServiceLogger.LOGGER.info("Response being built with Result: " + result);

        if (result == null || result.getStatus() == Response.Status.INTERNAL_SERVER_ERROR)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        return Response.status(result.getStatus()).entity(this).build();
    }

    public Response buildResponse(HashMap<String, String> headers) {
        ServiceLogger.LOGGER.info("Response being built with Result: " + result);

        if (result == null || result.getStatus() == Response.Status.INTERNAL_SERVER_ERROR)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        Response.ResponseBuilder builder = Response.status(result.getStatus()).entity(this);
        for(String headerName : headers.keySet()) {
            builder.header(headerName, headers.get(headerName));
        }
        return builder.build();
    }
}
