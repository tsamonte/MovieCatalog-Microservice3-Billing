package edu.uci.ics.tsamonte.service.billing.models.order.retrieve;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionFeeModel {
    @JsonProperty(value = "value", required = true)
    private String value;

    @JsonProperty(value = "currency", required = true)
    private String currency;

    @JsonCreator
    public TransactionFeeModel(@JsonProperty(value = "value", required = true) String value,
                               @JsonProperty(value = "currency", required = true) String currency) {
        this.value = value;
        this.currency = currency;
    }

    @JsonProperty(value = "value")
    public String getValue() {
        return value;
    }

    @JsonProperty(value = "currency")
    public String getCurrency() {
        return currency;
    }
}
