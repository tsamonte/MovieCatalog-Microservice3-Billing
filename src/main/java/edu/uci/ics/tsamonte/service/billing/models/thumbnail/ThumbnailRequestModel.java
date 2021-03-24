package edu.uci.ics.tsamonte.service.billing.models.thumbnail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailRequestModel {
    @JsonProperty(value = "movie_ids", required = true)
    private String[] movie_ids;

    @JsonCreator
    public ThumbnailRequestModel(@JsonProperty(value = "movie_ids", required = true) String[] movie_ids) {
        this.movie_ids = movie_ids;
    }

    @JsonProperty(value = "movie_ids")
    public String[] getMovie_ids() {
        return movie_ids;
    }
}
