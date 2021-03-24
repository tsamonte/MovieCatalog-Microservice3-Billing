package edu.uci.ics.tsamonte.service.billing.models.order.retrieve;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionModel {
    @JsonProperty(value = "capture_id", required = true)
    private String capture_id;

    @JsonProperty(value = "state", required = true)
    private String state;

    @JsonProperty(value = "amount")
    private AmountModel amount;

    @JsonProperty(value = "transaction_fee")
    private TransactionFeeModel transaction_fee;

    @JsonProperty(value = "create_time", required = true)
    private String create_time;

    @JsonProperty(value = "update_time", required = true)
    private String update_time;

    @JsonProperty(value = "items")
    private OrderItemModel[] items;

    @JsonCreator
    public TransactionModel(@JsonProperty(value = "capture_id", required = true) String capture_id,
                            @JsonProperty(value = "state", required = true) String state,
                            @JsonProperty(value = "amount") AmountModel amount,
                            @JsonProperty(value = "transaction_fee") TransactionFeeModel transaction_fee,
                            @JsonProperty(value = "create_time", required = true) String create_time,
                            @JsonProperty(value = "update_time", required = true) String update_time,
                            @JsonProperty(value = "items") OrderItemModel[] items) {
        this.capture_id = capture_id;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    @JsonProperty(value = "capture_id")
    public String getCapture_id() {
        return capture_id;
    }

    @JsonProperty(value = "state")
    public String getState() {
        return state;
    }

    @JsonProperty(value = "amount")
    public AmountModel getAmount() {
        return amount;
    }

    @JsonProperty(value = "transaction_fee")
    public TransactionFeeModel getTransaction_fee() {
        return transaction_fee;
    }

    @JsonProperty(value = "create_time")
    public String getCreate_time() {
        return create_time;
    }

    @JsonProperty(value = "update_time")
    public String getUpdate_time() {
        return update_time;
    }

    @JsonProperty(value = "items")
    public OrderItemModel[] getItems() {
        return items;
    }
}
