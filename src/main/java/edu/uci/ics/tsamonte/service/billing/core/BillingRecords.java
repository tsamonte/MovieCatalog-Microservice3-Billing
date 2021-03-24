package edu.uci.ics.tsamonte.service.billing.core;

import edu.uci.ics.tsamonte.service.billing.BillingService;
import edu.uci.ics.tsamonte.service.billing.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.billing.models.order.retrieve.OrderItemModel;
import edu.uci.ics.tsamonte.service.billing.models.request.EmailMIDQtyRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.request.EmailMIDRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.response.ItemModel;
import edu.uci.ics.tsamonte.service.billing.models.thumbnail.ThumbnailModel;
import edu.uci.ics.tsamonte.service.billing.models.thumbnail.ThumbnailRequestModel;
import edu.uci.ics.tsamonte.service.billing.models.thumbnail.ThumbnailResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;

public class BillingRecords {
    // ========================= CART/INSERT =========================
    public static int insertIntoCart(EmailMIDQtyRequestModel requestModel) {
        try {
            if(!movieExists(requestModel.getMovie_id())) return -1; // insert not successful
            String insertStatement = "INSERT INTO cart" +
                    " (email, movie_id, quantity)" +
                    " VALUES (?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(insertStatement);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovie_id());
            ps.setInt(3, requestModel.getQuantity());

            ServiceLogger.LOGGER.info("Trying insert: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert successful.");

            return 1; // insert successful
        }
        catch (SQLIntegrityConstraintViolationException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Duplicate insertion attempted.");
//            e.printStackTrace();
            return -2; // duplicate insertion
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Unable to insert into cart table.");
            e.printStackTrace();
            return -1; // insert not successful
        }
    }

    private static boolean movieExists(String movie_id) {
        try {
            String query = "SELECT *" +
                    " FROM movie_price" +
                    " WHERE movie_id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) return true;
            return false;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve records");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean itemIsInCart(EmailMIDRequestModel requestModel) {
        try {
            String query = "SELECT *" +
                    " FROM cart" +
                    " WHERE email = ? AND movie_id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovie_id());

            ServiceLogger.LOGGER.info("Trying query: "  + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rs.next()) {
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve cart records.");
            e.printStackTrace();
            return false;
        }
    }

    // ========================= CART/UPDATE =========================
    public static int updateCart(EmailMIDQtyRequestModel requestModel) {
        try {
            if(!itemIsInCart(requestModel)) return 0;
            String updateStatement = "UPDATE cart" +
                    " SET quantity = ?" +
                    " WHERE email = ? AND movie_id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(updateStatement);
            ps.setInt(1, requestModel.getQuantity());
            ps.setString(2, requestModel.getEmail());
            ps.setString(3, requestModel.getMovie_id());

            ServiceLogger.LOGGER.info("Trying update: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update successful.");
            return 1;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Update failed: Unable to update cart table.");
            e.printStackTrace();
            return -1;
        }
    }

    // ========================= CART/DELETE =========================
    public static int deleteItemInCart(EmailMIDRequestModel requestModel) {
        try {
            if(!itemIsInCart(requestModel)) return 0;
            String deleteStatement = "DELETE FROM cart" +
                    " WHERE email = ? AND movie_id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(deleteStatement);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovie_id());

            ServiceLogger.LOGGER.info("Trying delete: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Delete successful.");
            return 1;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Delete failed: Unable to delete cart record.");
            e.printStackTrace();
            return -1;
        }
    }

    // ========================= CART/RETRIEVE =========================
    public static ItemModel[] cartRetrieveQuery(String email) {
        try {
            HashMap<String, ItemModel> items = new HashMap<String, ItemModel>(); // key is movie_id

            // retrieve from cart to get movie_id array (and quantity)
            String cartQuery = "SELECT movie_id, quantity" +
                    " FROM cart" +
                    " WHERE email = ?;";
            PreparedStatement cartPS = BillingService.getCon().prepareStatement(cartQuery);
            cartPS.setString(1, email);

            ServiceLogger.LOGGER.info("Trying query: " + cartPS.toString());
            ResultSet cartRS = cartPS.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            while (cartRS.next()) {
                String movie_id = cartRS.getString("movie_id");
                int quantity = cartRS.getInt("quantity");

                ItemModel newModel = new ItemModel();
                newModel.setEmail(email);
                newModel.setMovie_id(movie_id);
                newModel.setQuantity(quantity);

                // retrieve from movie_prices to get unit_price and discount
                String priceQuery = "SELECT unit_price, discount" +
                        " FROM movie_price" +
                        " WHERE movie_id = ?;";
                PreparedStatement pricePS = BillingService.getCon().prepareStatement(priceQuery);
                pricePS.setString(1, movie_id);
                ResultSet priceRS = pricePS.executeQuery();
                priceRS.next();
                float unit_price = priceRS.getFloat("unit_price");
                float discount = priceRS.getFloat("discount");

                newModel.setUnit_price(unit_price);
                newModel.setDiscount(discount);
                //

                items.put(movie_id, newModel);
            }

            if(items.size() == 0) return null;

            // call movies/thumbnail using movie_id array to get title and paths
            // converting movie_id keyset to array to pass into request model
            String[] movie_ids = new String[items.size()];
            movie_ids = items.keySet().toArray(movie_ids);
            ThumbnailRequestModel thumbnailRequestModel = new ThumbnailRequestModel(movie_ids);

            // Path of endpoint
            String servicePath = BillingService.getMoviesConfigs().getScheme() + BillingService.getMoviesConfigs().getHostName() + ":" + BillingService.getMoviesConfigs().getPort() + BillingService.getMoviesConfigs().getPath();
            String endpointPath = BillingService.getMoviesConfigs().getThumbnailPath();

            ThumbnailResponseModel thumbnailResponseModel = MoviesCaller.makePost(servicePath, endpointPath, thumbnailRequestModel);
            for(ThumbnailModel model : thumbnailResponseModel.getThumbnails()) {
                items.get(model.getMovie_id()).setMovie_title(model.getTitle());
                items.get(model.getMovie_id()).setBackdrop_path(model.getBackdrop_path());
                items.get(model.getMovie_id()).setPoster_path(model.getPoster_path());
            }

            ItemModel[] finalItems = new ItemModel[items.size()];
            finalItems = items.values().toArray(finalItems);
            return finalItems;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve records.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================= CART/CLEAR =========================
    public static int clearCart(String email) {
        try {
            String deleteStatment = "DELETE FROM cart" +
                    " WHERE email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(deleteStatment);
            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Trying clear: " + ps.toString());
            int affectedRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Clear successful.");

            if(affectedRows > 0) return 1; // clear successful
            else return 0; // no items to be cleared
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Clear failed: Unable to clear cart.");
            e.printStackTrace();
            return -1; // operation failed (3150)
        }
    }

    // ========================= ORDER/PLACE =========================
    public static int insertIntoSale(ItemModel item) {
        try {
            String insertStatement = "INSERT INTO sale" +
                    " (email, movie_id, quantity, sale_date)" +
                    " VALUES (?, ?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(insertStatement);
            ps.setString(1, item.getEmail());
            ps.setString(2, item.getMovie_id());
            ps.setInt(3, item.getQuantity());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));

            ServiceLogger.LOGGER.info("Trying insert: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert successful.");

            String query = "SELECT sale_id" +
                    " FROM sale" +
                    " WHERE email = ? AND movie_id = ? AND quantity = ? AND sale_date = ?;";
            PreparedStatement retrievePS = BillingService.getCon().prepareStatement(query);
            retrievePS.setString(1, item.getEmail());
            retrievePS.setString(2, item.getMovie_id());
            retrievePS.setInt(3, item.getQuantity());
            retrievePS.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ResultSet rs = retrievePS.executeQuery();
            if(rs.next()) return rs.getInt("sale_id");
            else return -1;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Unable to insert into sale table.");
            e.printStackTrace();
            return -1;
        }
    }

    public static void insertIntoTransaction(int sale_id, String token) {
        try {
            String insertStatement = "INSERT INTO transaction" +
                    " (sale_id, token)" +
                    " VALUES (?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(insertStatement);
            ps.setInt(1, sale_id);
            ps.setString(2, token);

            ServiceLogger.LOGGER.info("Trying insert: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert successful.");
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Unable to insert into transaction table.");
            e.printStackTrace();
        }
    }

    // ========================= ORDER/COMPLETE =========================
    public static int updateTransaction(String token, String capture_id) {
        try {
            String updateStatement = "UPDATE transaction" +
                    " SET capture_id = ?" +
                    " WHERE token = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(updateStatement);
            ps.setString(1, capture_id);
            ps.setString(2, token);

            ServiceLogger.LOGGER.info("Trying update: " + ps.toString());
            int affectedRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update successful.");

            return affectedRows;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Update failed: Unable to update transaction table.");
            e.printStackTrace();
            return -1;
        }
    }

    public static String retrieveEmailFromToken(String token) {
        try {
            String query = "SELECT DISTINCT s.email" +
                    " FROM transaction AS t INNER JOIN sale AS s ON t.sale_id = s.sale_id" +
                    " WHERE t.token = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, token);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rs.next()) {
                return rs.getString("email");
            }
            return null;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve records.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================= ORDER/RETRIEVE =========================
    public static String getOrderIds(String email) {
        try {
            String query = "SELECT DISTINCT t.token" +
                    " FROM sale AS s INNER JOIN transaction AS t ON s.sale_id = t.sale_id" +
                    " WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rs.next()) return rs.getString("token");
            else return null;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve transaction records");
            e.printStackTrace();
            return null;
        }
    }

    public static OrderItemModel[] retrieveOrders(String email) {
        try {
            HashMap<String, OrderItemModel> items = new HashMap<String ,OrderItemModel>(); // key is movie_id

            // retrieve from sale to get movie_ids, quantity, and sale_date
            String saleQuery = "SELECT movie_id, quantity, sale_date" +
                    " FROM sale" +
                    " WHERE email = ?;";
            PreparedStatement cartPS = BillingService.getCon().prepareStatement(saleQuery);
            cartPS.setString(1, email);

            ServiceLogger.LOGGER.info("Trying query: " + cartPS.toString());
            ResultSet saleRS = cartPS.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            while (saleRS.next()) {
                String movie_id = saleRS.getString("movie_id");
                int quantity = saleRS.getInt("quantity");
                String sale_date = saleRS.getDate("sale_date").toString();

                OrderItemModel newModel = new OrderItemModel();
                newModel.setEmail(email);
                newModel.setMovie_id(movie_id);
                newModel.setQuantity(quantity);
                newModel.setSale_date(sale_date);

                // retrieve from movie_prices to get unit_price and discount
                String priceQuery = "SELECT unit_price, discount" +
                        " FROM movie_price" +
                        " WHERE movie_id = ?;";
                PreparedStatement pricePS = BillingService.getCon().prepareStatement(priceQuery);
                pricePS.setString(1, movie_id);
                ResultSet priceRS = pricePS.executeQuery();
                priceRS.next();
                float unit_price = priceRS.getFloat("unit_price");
                float discount = priceRS.getFloat("discount");

                newModel.setUnit_price(unit_price);
                newModel.setDiscount(discount);
                //

                items.put(movie_id, newModel);
            }

            if(items.size() == 0) return null;

            OrderItemModel[] finalItems = new OrderItemModel[items.size()];
            finalItems = items.values().toArray(finalItems);
            return finalItems;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve records.");
            e.printStackTrace();
            return null;
        }
    }
}
