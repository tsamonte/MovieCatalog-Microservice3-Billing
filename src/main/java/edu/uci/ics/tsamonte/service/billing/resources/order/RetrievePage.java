package edu.uci.ics.tsamonte.service.billing.resources.order;

import com.braintreepayments.http.HttpResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.orders.*;
import edu.uci.ics.tsamonte.service.billing.BillingService;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.core.BillingRecords;
import edu.uci.ics.tsamonte.service.billing.core.PaypalOrderClient;
import edu.uci.ics.tsamonte.service.billing.models.order.retrieve.*;
import edu.uci.ics.tsamonte.service.billing.models.request.EmailRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.response.ResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@Path("order")
public class RetrievePage {
    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveResponse(@Context HttpHeaders headers, String jsonText) {
        EmailRequestModel requestModel;
        OrderRetrieveResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
        }
        catch (IOException e){
            e.printStackTrace();

            // resultCode = -3; 400 Bad request; "JSON Parse Exception."
            if(e instanceof JsonParseException) {
                responseModel = new OrderRetrieveResponseModel(Result.JSON_PARSE_EXCEPTION, null);
            }

            // resultCode = -2; 400 Bad request; "JSON Mapping Exception."
            else if (e instanceof JsonMappingException) {
                responseModel = new OrderRetrieveResponseModel(Result.JSON_MAPPING_EXCEPTION, null);
            }

            else {
                responseModel = new OrderRetrieveResponseModel(Result.INTERNAL_SERVER_ERROR, null);
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

            OrderItemModel[] items = BillingRecords.retrieveOrders(requestModel.getEmail());
            // resultCode = 313; 200 Status OK; "Order history does not exist"
            if(items == null) {
                responseModel = new OrderRetrieveResponseModel(Result.ORDER_HISTORY_DNE, null);
                return responseModel.buildResponse(headerMap);
            }

            String orderId = BillingRecords.getOrderIds(requestModel.getEmail());
            HttpResponse<Order> response = PaypalOrderClient.getOrder(orderId);

            String state = response.result().status().toLowerCase();

            Capture capture = response.result().purchaseUnits().get(0).payments().captures().get(0);
            String captureId = capture.id();
            AmountWithBreakdown amountDescription = response.result().purchaseUnits().get(0).amountWithBreakdown();
            AmountModel amount = new AmountModel(amountDescription.value(), amountDescription.currencyCode());

            Money transactionDescription = capture.sellerReceivableBreakdown().paypalFee();
            TransactionFeeModel transactionFee = new TransactionFeeModel(transactionDescription.value(), transactionDescription.currencyCode());

            String createTime = capture.createTime();
            String updateTime = capture.updateTime();

            TransactionModel[] transactions = {new TransactionModel(captureId, state, amount, transactionFee,
                    createTime, updateTime, items)};

            responseModel = new OrderRetrieveResponseModel(Result.ORDER_RETRIEVE_SUCCESS, transactions);
            return responseModel.buildResponse(headerMap);
        }
        catch (Exception e) {
            return null;
        }
    }
}
