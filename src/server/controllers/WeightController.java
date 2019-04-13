package server.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.DatabaseConnection;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static server.Convertor.convertToJSONArray;


@Path("weight/")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)

public class WeightController {

    @GET
    @Path("list")
    public String weightList(@CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked WeightController.list()");

        if (sessionCookie == null) {
            JSONObject response = new JSONObject();
            response.put("Error:", "Something as gone wrong.  Please contact the administrator with the error code WC-WL.");
            return response.toString();
        }

        try {
            int userID = UserController.validateSessionCookie(sessionCookie);
            PreparedStatement statement = DatabaseConnection.newStatement(
                    "SELECT WeightID, Date, WeightInKG, UserID FROM Weights WHERE userID = ? order by Date DESC"
            );
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            JSONArray newJSONArray = convertToJSONArray(resultSet);
            System.out.println(newJSONArray.toString());
            return newJSONArray.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            JSONObject response = new JSONObject();
            response.put("Error:", "Something as gone wrong.  Please contact the administrator with the error code WC-WL.");
            return response.toString();
        }


    }


    @POST
    @Path("delete")
    @Produces(MediaType.TEXT_PLAIN)
    public String weightDelete(@FormParam("weightID") int weightID,
                               @CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked WeightController.delete()");

        if (sessionCookie == null) {
            return "Error: Something as gone wrong.  Please contact the administrator with the error code WC-WD.";
        }

        try {
            int userID = UserController.validateSessionCookie(sessionCookie);
            PreparedStatement statement = DatabaseConnection.newStatement(
                    "DELETE FROM Weights WHERE WeightID = ?"
            );
            statement.setInt(1, weightID);
            statement.executeUpdate();
            return "OK";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error: Something as gone wrong.  Please contact the administrator with the error code WC-WD.";
        }

    }


    @POST
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public String weightAdd(@FormParam("date") String date,
                            @FormParam("weightInKG") int weightInKG,
                            @CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked WeightController.weightAdd()");

        if (sessionCookie == null) {
            return "Error: Something as gone wrong.  Please contact the administrator with the error code WC-WA.";
        }

        try {
            int userID = UserController.validateSessionCookie(sessionCookie);
            PreparedStatement statement = DatabaseConnection.newStatement(
                    "INSERT INTO Weights (Date, WeightInKG, UserID) VALUES (?, ?, ?)"          //database sets WeightID when record created so omitted in SQL
            );
            statement.setString(1, date);
            statement.setInt(2, weightInKG);
            statement.setInt(3, userID);
            statement.executeUpdate();
            return "OK";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error: Something as gone wrong.  Please contact the administrator with the error code WC-WA.";
        }

    }

}

