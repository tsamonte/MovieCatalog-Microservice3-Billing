package edu.uci.ics.tsamonte.service.billing.resources.order;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.orders.Order;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.core.BillingRecords;
import edu.uci.ics.tsamonte.service.billing.core.PaypalOrderClient;
import edu.uci.ics.tsamonte.service.billing.models.request.EmailRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.response.ItemModel;
import edu.uci.ics.tsamonte.service.billing.models.response.OrderPlaceResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@Path("order")
public class PlacePage {
    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderPlaceResponse(@Context HttpHeaders headers, String jsonText) {
        EmailRequestModel requestModel;
        OrderPlaceResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
        }
        catch (IOException e){
            e.printStackTrace();

            // resultCode = -3; 400 Bad request; "JSON Parse Exception."
            if(e instanceof JsonParseException) {
                responseModel = new OrderPlaceResponseModel(Result.JSON_PARSE_EXCEPTION, null, null);
            }

            // resultCode = -2; 400 Bad request; "JSON Mapping Exception."
            else if (e instanceof JsonMappingException) {
                responseModel = new OrderPlaceResponseModel(Result.JSON_MAPPING_EXCEPTION, null, null);
            }

            else {
                responseModel = new OrderPlaceResponseModel(Result.INTERNAL_SERVER_ERROR, null, null);
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

            // retrieve cart
            ItemModel[] items = BillingRecords.cartRetrieveQuery(requestModel.getEmail());

            // resultCode = 312; 200 Status OK; "Shopping cart item does not exist."
            if (items == null) {
                responseModel = new OrderPlaceResponseModel(Result.ITEM_DOES_NOT_EXIST, null, null);
                return responseModel.buildResponse(headerMap);
            }

            float cartTotal = 0;
            for (ItemModel item : items) {
                cartTotal += item.getQuantity() * (item.getUnit_price() - item.getDiscount());
            }
            // Creating PayPal order
            PaypalOrderClient ppOrderClient = new PaypalOrderClient();
            Order order = ppOrderClient.createPayPalOrder(ppOrderClient, Float.toString(cartTotal));
            String token = order.id();
            String approve_url = PaypalOrderClient.getApproveURL(order);

            for (ItemModel item : items) {
                // Insert into sale
                int sale_id = BillingRecords.insertIntoSale(item);
                // Insert into transaction
                BillingRecords.insertIntoTransaction(sale_id, token);
            }

            // resultCode = 3400; 200 Status OK; "Order placed successfully."
            responseModel = new OrderPlaceResponseModel(Result.ORDER_PLACE_SUCCESS, approve_url, token);
            return responseModel.buildResponse(headerMap);
        }
        catch (Exception e) {
            responseModel = new OrderPlaceResponseModel(Result.ORDER_CREATION_FAILED, null, null);
            return responseModel.buildResponse();
        }
    }
}
