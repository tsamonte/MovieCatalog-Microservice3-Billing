package edu.uci.ics.tsamonte.service.billing.resources.cart;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.tsamonte.service.billing.BillingService;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.core.BillingRecords;
import edu.uci.ics.tsamonte.service.billing.core.IdmCaller;
import edu.uci.ics.tsamonte.service.billing.models.privilege.PrivilegeRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.privilege.PrivilegeResponseModel;
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
public class InsertPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertResponse(@Context HttpHeaders headers, String jsonText) {
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

        // ==================== Calling idm/privilege ===================
        // Path of endpoint
        String servicePath = "http://" + BillingService.getIdmConfigs().getHostName() + ":" + BillingService.getIdmConfigs().getPort() + BillingService.getIdmConfigs().getPath();
        String endpointPath = "/privilege";

        // Get header strings
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // Place headers into a HashMap
        HashMap<String,String> headerMap = new HashMap<String, String>();
        headerMap.put("email", email);
        headerMap.put("session_id", session_id);
        headerMap.put("transaction_id", transaction_id);

        PrivilegeRequestModel privilegeRequest = new PrivilegeRequestModel(email, 5);
        PrivilegeResponseModel privilegeResponse = IdmCaller.makePost(servicePath, endpointPath, privilegeRequest);

        // ==================== Main functionality of endpoint ===================
        // resultCode = 14; 200 Status OK; "User not found."
        if(privilegeResponse.getResultCode() == 14) {
            responseModel = new ResponseModel(Result.USER_NOT_FOUND);
            return responseModel.buildResponse(headerMap);
        }

        // resultCode = 33; 200 Status OK; "Quantity has invalid value"
        if(requestModel.getQuantity() <= 0) {
            responseModel = new ResponseModel(Result.INVALID_QUANTITY);
            return responseModel.buildResponse(headerMap);
        }


        int insertResult = BillingRecords.insertIntoCart(requestModel);
        // resultCode = 3100; 200 Status OK; "Shopping cart item inserted successfully."
        if(insertResult == 1) {
            responseModel = new ResponseModel(Result.SUCCESSFUL_INSERTION);
        }
        // resultCode = 311; 200 Status OK; "Duplicate insertion."
        else if(insertResult == -2) {
            responseModel = new ResponseModel(Result.DUPLICATE_INSERTION);
        }
        // resultCode = 3150; 200 Status OK; "Shopping cart operation failed."
        else {
            responseModel = new ResponseModel(Result.CART_OPERATION_FAILED);
        }

        return responseModel.buildResponse(headerMap);
    }
}
