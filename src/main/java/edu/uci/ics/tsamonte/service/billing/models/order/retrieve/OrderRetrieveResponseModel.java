package edu.uci.ics.tsamonte.service.billing.models.order.retrieve;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.billing.base.Result;
import edu.uci.ics.tsamonte.service.billing.models.response.ResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel extends ResponseModel {
    @JsonProperty(value = "transactions")
    private TransactionModel[] transactions;

    @JsonCreator
    public OrderRetrieveResponseModel(Result result,
                                      @JsonProperty(value = "transactions") TransactionModel[] transactions) {
        super(result);
        this.transactions = transactions;
    }

    @JsonProperty(value = "transactions")
    public TransactionModel[] getTransactions() {
        return transactions;
    }
}
