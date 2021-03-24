package edu.uci.ics.tsamonte.service.billing.resources.order;

import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.core.BillingRecords;
import edu.uci.ics.tsamonte.service.billing.core.PaypalOrderClient;
import edu.uci.ics.tsamonte.service.billing.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.billing.models.response.ResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("order")
public class CompletePage {
    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeResponse(@QueryParam("token") String token,
                                     @QueryParam("PayerID") String payer_id) {
        ResponseModel responseModel;

        String capture_id = PaypalOrderClient.captureOrder(token);
        ServiceLogger.LOGGER.info("Capture id: " + capture_id);
        int affectedRows = BillingRecords.updateTransaction(token, capture_id);

        if(affectedRows > 0) {
            responseModel = new ResponseModel(Result.ORDER_COMPLETE_SUCCESS);
        }
        else if(affectedRows == 0) {
            responseModel = new ResponseModel(Result.TOKEN_NOT_FOUND);
        }
        else {
            responseModel = new ResponseModel(Result.ORDER_CANT_COMPLETE);
        }

        // clear cart
        String email = BillingRecords.retrieveEmailFromToken(token);
        if(email != null) BillingRecords.clearCart(email);
        else ServiceLogger.LOGGER.warning("Cannot clear cart. Email does not exist");

        return responseModel.buildResponse();
    }
}
