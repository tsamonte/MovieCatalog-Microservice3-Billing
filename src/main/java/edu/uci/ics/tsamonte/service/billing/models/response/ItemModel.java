package edu.uci.ics.tsamonte.service.billing.models.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "unit_price", required = true)
    private float unit_price;

    @JsonProperty(value = "discount", required = true)
    private float discount;

    @JsonProperty(value = "quantity", required = true)
    private int quantity;

    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonProperty(value = "movie_title", required = true)
    private String movie_title;

    @JsonProperty(value = "backdrop_path")
    private String backdrop_path;

    @JsonProperty(value = "poster_path")
    private String poster_path;

    public ItemModel() {}

    @JsonCreator
    public ItemModel(@JsonProperty(value = "email", required = true) String email,
                     @JsonProperty(value = "unit_price", required = true) float unit_price,
                     @JsonProperty(value = "discount", required = true) float discount,
                     @JsonProperty(value = "quantity", required = true) int quantity,
                     @JsonProperty(value = "movie_id", required = true) String movie_id,
                     @JsonProperty(value = "movie_title", required = true) String movie_title,
                     @JsonProperty(value = "backdrop_path") String backdrop_path,
                     @JsonProperty(value = "poster_path") String poster_path) {
        this.email = email;
        this.unit_price = unit_price;
        this.discount = discount;
        this.quantity = quantity;
        this.movie_id = movie_id;
        this.movie_title = movie_title;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;
    }

    @JsonProperty(value = "email")
    public String getEmail() { return email; }
    @JsonProperty(value = "unit_price")
    public float getUnit_price() { return unit_price; }
    @JsonProperty(value = "discount", required = true)
    public float getDiscount() { return discount; }
    @JsonProperty(value = "quantity", required = true)
    public int getQuantity() { return quantity; }
    @JsonProperty(value = "movie_id", required = true)
    public String getMovie_id() { return movie_id; }
    @JsonProperty(value = "movie_title", required = true)
    public String getMovie_title() { return movie_title; }
    @JsonProperty(value = "backdrop_path")
    public String getBackdrop_path() { return backdrop_path; }
    @JsonProperty(value = "poster_path")
    public String getPoster_path() { return poster_path; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUnit_price(float unit_price) {
        this.unit_price = unit_price;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
}
