package controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("review/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Review {
    @GET
    @Path("list")
    public String reviewList() {
        System.out.println("Invoked Review.reviewList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT ReviewID, ReviewerName, AccommodationName, ReviewRating, ReviewText FROM Reviews");
            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();
                row.put("ReviewID", results.getInt(1));
                row.put("ReviewerName", results.getString(2));
                row.put("AccommodationName", results.getString(3));
                row.put("ReviewRating", results.getInt(4));
                row.put("ReviewText", results.getString(5));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items. \"}";
        }
    }
}
