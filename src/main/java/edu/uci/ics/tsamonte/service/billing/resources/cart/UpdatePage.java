package edu.uci.ics.tsamonte.service.billing.resources.cart;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.core.BillingRecords;
import edu.uci.ics.tsamonte.service.billing.models.request.EmailMIDQtyRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.response.ResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@Path("cart")
public class UpdatePage {
    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateResponse(@Context HttpHeaders headers, String jsonText) {
        EmailMIDQtyRequestModel requestModel;
        ResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, EmailMIDQtyRequestModel.class);
        }
        catch (IOException e){
            e.printStackTrace();

            // resultCode = -3; 400 Bad request; "JSON Parse Exception."
            if(e instanceof JsonParseException) {
                responseModel = new ResponseModel(Result.JSON_PARSE_EXCEPTION);
            }

            // resultCode = -2; 400 Bad request; "JSON Mapping Exception."
            else if (e instanceof JsonMappingException) {
                responseModel = new ResponseModel(Result.JSON_MAPPING_EXCEPTION);
            }

            else {
                responseModel = new ResponseModel(Result.INTERNAL_SERVER_ERROR);
            }
            return responseModel.buildResponse();
        }

        // ==================== Retrieve headers ===================
        // Get header strings
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // Place headers into a HashMap
        HashMap<String,String> headerMap = new HashMap<String, String>();
        headerMap.put("email", email);
        headerMap.put("session_id", session_id);
        headerMap.put("transaction_id", transaction_id);

        // ==================== Main functionality of endpoint ===================
        // resultCode = 33; 200 Status OK; "Quantity has invalid value"
        if(requestModel.getQuantity() <= 0) {
            responseModel = new ResponseModel(Result.INVALID_QUANTITY);
            return responseModel.buildResponse(headerMap);
        }

        int cartResponse = BillingRecords.updateCart(requestModel);
        // resultCode = 312; 200 Status OK; "Shopping cart item does not exist"
        if(cartResponse == 0) {
            responseModel = new ResponseModel(Result.ITEM_DOES_NOT_EXIST);
        }
        // resultCode = 3110; 200 Status OK; "Shopping cart item updated successfully."
        else if(cartResponse == 1) {
            responseModel = new ResponseModel(Result.SUCCESSFUL_UPDATE);
        }
        // resultCode = 3150; 200 Status OK; "Shopping cart operation failed."
        else {
            responseModel = new ResponseModel(Result.CART_OPERATION_FAILED);
        }

        return responseModel.buildResponse(headerMap);
    }
}
