package edu.uci.ics.tsamonte.service.billing.models.privilege;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrivilegeRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "plevel", required = true)
    private int plevel;

    @JsonCreator
    public PrivilegeRequestModel(@JsonProperty(value = "email", required = true) String email,
                                 @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "plevel")
    public int getPlevel() {
        return plevel;
    }
}
