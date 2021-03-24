package edu.uci.ics.tsamonte.service.billing.models.order.retrieve;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonProperty(value = "quantity", required = true)
    private int quantity;

    @JsonProperty(value = "unit_price", required = true)
    private float unit_price;

    @JsonProperty(value = "discount", required = true)
    private float discount;

    @JsonProperty(value = "sale_date", required = true)
    private String sale_date;

    public OrderItemModel() {}

    @JsonCreator
    public OrderItemModel(@JsonProperty(value = "email", required = true) String email,
                          @JsonProperty(value = "movie_id", required = true) String movie_id,
                          @JsonProperty(value = "quantity", required = true) int quantity,
                          @JsonProperty(value = "unit_price", required = true) float unit_price,
                          @JsonProperty(value = "discount", required = true) float discount,
                          @JsonProperty(value = "sale_date", required = true) String sale_date) {
        this.email = email;
        this.movie_id = movie_id;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this. discount = discount;
        this.sale_date = sale_date;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "movie_id")
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty(value = "quantity")
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty(value = "unit_price")
    public float getUnit_price() {
        return unit_price;
    }

    @JsonProperty(value = "discount")
    public float getDiscount() {
        return discount;
    }

    @JsonProperty(value = "sale_date")
    public String getSale_date() {
        return sale_date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit_price(float unit_price) {
        this.unit_price = unit_price;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setSale_date(String sale_date) {
        this.sale_date = sale_date;
    }
}
