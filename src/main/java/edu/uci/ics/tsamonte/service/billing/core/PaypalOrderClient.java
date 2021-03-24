package edu.uci.ics.tsamonte.service.billing.core;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.braintreepayments.http.serializer.Json;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import edu.uci.ics.tsamonte.service.billing.BillingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaypalOrderClient {
    private static final String clientId = "AdMpjPJCNxXnASrkxTCSme-9OdEoh1tHYeECZ-2Mz9imPH7YE7qoR9uAEwUmDQi6fpRSk7WxfCLNzhmj";
    private static final String clientSecret = "EAzW5XvKthQBR-GNoJ8asgnW67w3qg62KIwE21Z0bgq_7Rv1IS7YllXLVWIJAyqihqJv5pVsg7dJNk2B";

    // Set up paypal environment
    public static PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);

    // Create client for environment
    public static PayPalHttpClient client = new PayPalHttpClient(environment);

    public Order createPayPalOrder(PaypalOrderClient client, String total) {
        Order order;

        // Construct a request object and set desired parameters
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // Create application context with return url upon payer completion
        ApplicationContext applicationContext = new ApplicationContext().returnUrl("http://localhost:3000/complete");// "http://0.0.0.0:12345/api/billing/order/complete");
        // Add ApplicationContext to order request
        orderRequest.applicationContext(applicationContext);

        // Create purchase units/movie purchase list (grabbed from cart)
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(new PurchaseUnitRequest().amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(total)));
        orderRequest.purchaseUnits(purchaseUnits);

        // Create an OrdersCreateRequestObject
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        // Call API
        try {
            HttpResponse<Order> response = client.client.execute(request);

            order = response.result();

            // Retrieve order_id (same as token) for table
            System.out.println("Order ID: " + order.id());

            order.links().forEach(link -> System.out.println(link.rel() + " => " + link.method() + ":" + link.href()));

            return order;
        }
        catch(IOException e) {
            System.err.println("************COULD NOT CREATE ORDER************");

            if(e instanceof HttpException) {
                HttpException he = (HttpException) e;
                System.out.println(he.getMessage());
                he.headers().forEach(x  -> System.out.println(x + " :" + he.headers().header(x)));
            }
            return null;
        }
    }

    public static String getApproveURL(Order order) {
//        order.links().forEach(link -> System.out.println(link.rel() + " => " + link.method() + ":" + link.href()));
        for(LinkDescription link : order.links()) {
            if(link.rel().equals("approve")) return link.href();
        }
        return null;
    }

    public static String captureOrder(String orderID) {
        Order order;
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderID);

        // call API
        try {
            // call API with your client and get a response for your call
            HttpResponse<Order> response = client.execute(request);
            order = response.result();

            // retrieve capture_id
            String captureID = order.purchaseUnits().get(0).payments().captures().get(0).id();
            System.out.println("Capture ID: " + captureID);
            return captureID;
//            return order;

        }
        catch (IOException e) {
            if(e instanceof HttpException) {
                HttpException he = (HttpException)e;
                System.out.println(he.getMessage());
                he.headers().forEach(x -> System.out.println(x + " : " + he.headers().header(x)));
            }
            return null;
        }
    }

    public static HttpResponse<Order> getOrder(String orderId) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(orderId);
        HttpResponse<Order> response = client.execute(request);
        String json = new Json().serialize(response.result());
        System.out.println("Full response body: " + json);
        System.out.println(response.result());
        return response;
    }
}
