package edu.uci.ics.tsamonte.service.billing.models.order.retrieve;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmountModel {
    @JsonProperty(value = "total", required = true)
    private String total;

    @JsonProperty(value = "currency", required = true)
    private String currency;

    @JsonCreator
    public AmountModel(@JsonProperty(value = "total", required = true) String total,
                       @JsonProperty(value = "currency", required = true) String currency) {
        this.total = total;
        this.currency = currency;
    }

    @JsonProperty(value = "total")
    public String getTotal() {
        return total;
    }

    @JsonProperty(value = "currency")
    public String getCurrency() {
        return currency;
    }
}
