package edu.uci.ics.tsamonte.service.billing.base;

import javax.ws.rs.core.Response.Status;

public enum Result
{
    JSON_PARSE_EXCEPTION   (-3, "JSON Parse Exception.",   Status.BAD_REQUEST),
    JSON_MAPPING_EXCEPTION (-2, "JSON Mapping Exception.", Status.BAD_REQUEST),

    INTERNAL_SERVER_ERROR  (-1, "Internal Server Error.",  Status.INTERNAL_SERVER_ERROR),

    USER_NOT_FOUND          (14, "User not found.", Status.OK),
    INVALID_QUANTITY        (33, "Quantity has invalid value.", Status.OK),
    DUPLICATE_INSERTION     (311, "Duplicate insertion.", Status.OK),
    ITEM_DOES_NOT_EXIST     (312, "Shopping cart item does not exist.", Status.OK),
    SUCCESSFUL_INSERTION    (3100, "Shopping cart item inserted successfully.", Status.OK),
    SUCCESSFUL_UPDATE       (3110, "Shopping cart item updated successfully.", Status.OK),
    SUCCESSFUL_DELETE       (3120, "Shopping cart item deleted successfully.", Status.OK),
    CART_RETRIEVE_SUCCESS   (3130, "Shopping cart retrieved successfully.", Status.OK),
    SUCCESSFUL_CLEAR        (3140, "Shopping cart cleared successfully.", Status.OK),
    CART_OPERATION_FAILED   (3150, "Shopping cart operation failed.", Status.OK),

    ORDER_CREATION_FAILED   (342, "Order creation failed.", Status.OK),
    ORDER_HISTORY_DNE       (313, "Order history does not exist.", Status.OK),
    ORDER_PLACE_SUCCESS     (3400, "Order placed successfully.", Status.OK),
    ORDER_RETRIEVE_SUCCESS  (3410, "Order retrieved successfully.", Status.OK),
    ORDER_COMPLETE_SUCCESS  (3420, "Order is completed successfully.", Status.OK),
    TOKEN_NOT_FOUND         (3421, "Token not found.", Status.OK),
    ORDER_CANT_COMPLETE     (3422, "Order can not be completed.", Status.OK);
//    FOUND_MOVIES            (210, "Found movie(s) with search parameters.", Status.OK),
//    NO_MOVIES_FOUND         (211, "No movies found with search parameters.", Status.OK),
//    PEOPLE_FOUND            (212, "Found people with search parameters.", Status.OK),
//    NO_PEOPLE_FOUND         (213, "No people found with search parameters.", Status.OK);

    private final int    resultCode;
    private final String message;
    private final Status status;

    Result(int resultCode, String message, Status status)
    {
        this.resultCode = resultCode;
        this.message = message;
        this.status = status;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public String getMessage()
    {
        return message;
    }

    public Status getStatus()
    {
        return status;
    }
}
