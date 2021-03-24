package edu.uci.ics.tsamonte.service.billing.models.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.billing.base.Result;

public class OrderPlaceResponseModel extends ResponseModel {
    @JsonProperty(value = "approve_url")
    private String approve_url;

    @JsonProperty(value = "token")
    private String token;

    @JsonCreator
    public OrderPlaceResponseModel(Result result,
                                   @JsonProperty(value = "approve_url") String approve_url,
                                   @JsonProperty(value = "token") String token) {
        super(result);
        this.approve_url = approve_url;
        this.token = token;
    }

    @JsonProperty(value = "approve_url")
    public String getApprove_url() {
        return approve_url;
    }

    @JsonProperty(value = "token")
    public String getToken() {
        return token;
    }
}
