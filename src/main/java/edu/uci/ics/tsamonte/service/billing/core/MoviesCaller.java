package edu.uci.ics.tsamonte.service.billing.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.tsamonte.service.billing.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.billing.models.thumbnail.ThumbnailRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.thumbnail.ThumbnailResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class MoviesCaller {
    public static ThumbnailResponseModel makePost(String servicePath, String endpointPath, ThumbnailRequestModel requestModel) {
        ThumbnailResponseModel responseModel = null;

        // Create a client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request and save it to a response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, ThumbnailResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO");
        }
        catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
        }

        return responseModel;
    }
}
