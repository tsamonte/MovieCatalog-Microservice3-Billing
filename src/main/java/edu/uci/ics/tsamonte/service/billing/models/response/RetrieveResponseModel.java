package edu.uci.ics.tsamonte.service.billing.models.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.billing.base.Result;

public class RetrieveResponseModel extends ResponseModel {
    @JsonProperty(value = "items")
    private ItemModel[] items;

    @JsonCreator
    public RetrieveResponseModel(Result result, @JsonProperty(value = "items") ItemModel[] items) {
        super(result);
        this.items = items;
    }

    @JsonProperty(value = "items")
    public ItemModel[] getItems() { return items; }
}
