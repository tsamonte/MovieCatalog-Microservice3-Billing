package edu.uci.ics.tsamonte.service.billing.models.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailMIDRequestModel extends EmailRequestModel {
    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonCreator
    public EmailMIDRequestModel(@JsonProperty(value = "email", required = true) String email,
                                @JsonProperty(value = "movie_id", required = true) String movie_id) {
        super(email);
        this.movie_id = movie_id;
    }

    @JsonProperty(value = "movie_id")
    public String getMovie_id() {
        return movie_id;
    }
}
