package edu.uci.ics.tsamonte.service.billing.resources.cart;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.core.BillingRecords;
import edu.uci.ics.tsamonte.service.billing.models.response.ItemModel;
import edu.uci.ics.tsamonte.service.billing.models.request.EmailRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.response.RetrieveResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@Path("cart")
public class RetrievePage {
    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveResponse(@Context HttpHeaders headers, String jsonText) {
        EmailRequestModel requestModel;
        RetrieveResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
        }
        catch (IOException e) {
            e.printStackTrace();

            // resultCode = -3; 400 Bad request; "JSON Parse Exception."
            if(e instanceof JsonParseException) {
                responseModel = new RetrieveResponseModel(Result.JSON_PARSE_EXCEPTION, null);
            }

            // resultCode = -2; 400 Bad request; "JSON Mapping Exception."
            else if (e instanceof JsonMappingException) {
                responseModel = new RetrieveResponseModel(Result.JSON_MAPPING_EXCEPTION, null);
            }

            else {
                responseModel = new RetrieveResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            }
            return responseModel.buildResponse();
        }

        try {
            // Get header strings
            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            // Place headers into a HashMap
            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("email", email);
            headerMap.put("session_id", session_id);
            headerMap.put("transaction_id", transaction_id);

            ItemModel[] items = BillingRecords.cartRetrieveQuery(requestModel.getEmail());

            // resultCode = 312; 200 Status OK; "Shopping cart item does not exist."
            if (items == null) {
                responseModel = new RetrieveResponseModel(Result.ITEM_DOES_NOT_EXIST, null);
            } else {
                responseModel = new RetrieveResponseModel(Result.CART_RETRIEVE_SUCCESS, items);
            }

            return responseModel.buildResponse(headerMap);
        }
        catch (Exception e) {
            responseModel = new RetrieveResponseModel(Result.CART_OPERATION_FAILED, null);
            return responseModel.buildResponse();
        }
    }
}
