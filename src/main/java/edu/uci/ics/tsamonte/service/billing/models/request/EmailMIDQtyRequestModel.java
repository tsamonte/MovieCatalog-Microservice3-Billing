package edu.uci.ics.tsamonte.service.billing.models.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailMIDQtyRequestModel extends EmailMIDRequestModel {
    @JsonProperty(value = "quantity", required = true)
    private int quantity;

    @JsonCreator
    public EmailMIDQtyRequestModel(@JsonProperty(value = "email", required = true) String email,
                                   @JsonProperty(value = "movie_id", required = true) String movie_id,
                                   @JsonProperty(value = "quantity", required = true) int quantity) {
        super(email, movie_id);
        this.quantity = quantity;
    }

    @JsonProperty(value = "quantity")
    public int getQuantity() {
        return quantity;
    }
}
