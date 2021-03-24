package edu.uci.ics.tsamonte.service.billing.models.thumbnail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "thumbnails", required = true)
    private ThumbnailModel[] thumbnails;

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                  @JsonProperty(value = "message", required = true) String message,
                                  @JsonProperty(value = "thumbnails", required = true) ThumbnailModel[] thumbnails) {
        this.resultCode = resultCode;
        this.message = message;
        this.thumbnails = thumbnails;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "thumbnails")
    public ThumbnailModel[] getThumbnails() {
        return thumbnails;
    }
}
