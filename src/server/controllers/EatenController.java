package server.controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.DatabaseConnection;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static server.Convertor.convertToJSONArray;


@Path("eaten/")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)

public class EatenController {

    @GET
    @Path("list")
    public String eatenList(@QueryParam("dateEaten") String dateEaten, @CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked EatenController.eatenList() + dateEaten= " + dateEaten + " and sessionCookie= " + sessionCookie.getValue());

        if (sessionCookie == null) {
            return "Error: Something as gone wrong.  Please contact the administrator with the error code EC-ED";
        }

        try {
            int userID = UserController.validateSessionCookie(sessionCookie);   //get the userID for the user as we only want to return the foods they have eaten

            PreparedStatement statement = DatabaseConnection.newStatement(
                    "SELECT Eaten.EatenID, " +
                            "Eaten.MealName, " +
                            "Eaten.Serving, " +
                            "Eaten.DateEaten, " +
                            "Eaten.FoodID, " +
                            "Eaten.UserID, " +
                            "CalsPerServing * Serving AS TotalCals, " +
                            "round(FatPerServing * Serving, 2) AS TotalFat, " +
                            "round(SatFatPerServing * Serving, 2) AS TotalSatFat, " +
                            "round(CarbsPerServing * Serving, 2) AS TotalCarbs, " +
                            "round(SugarsPerServing * Serving, 2) AS TotalSugar, " +
                            "round(FibrePerServing * Serving, 2) AS TotalFibre, " +
                            "round(ProteinPerServing * Serving, 2) AS TotalProtein, " +
                            "round(SaltPerServing * Serving, 2) AS TotalSalt " +
                            "FROM Eaten JOIN Foods ON (Foods.FoodID = Eaten.FoodID) " +
                            "WHERE userID = ? AND DateEaten = ?"
            );
            statement.setInt(1, userID);
            statement.setString(2, dateEaten);
            ResultSet resultSet = statement.executeQuery();

            JSONArray response = convertToJSONArray(resultSet);   //convert ResultSet to JSON array for the browser
            System.out.println(response.toString());              //output so we can see the results on the console
            return response.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());   //print the exception error message so it can be used to rectify the problem.  Do not send this back to client!
            JSONObject response = new JSONObject();
            response.put("Error:", "Something as gone wrong.  Please contact the administrator with the error code EC-EL. ");
            return response.toString();

        }
    }


    @POST
    @Path("delete")
    @Produces(MediaType.TEXT_PLAIN)
    public String eatenDelete(@FormParam("eatenID") int eatenID,
                              @CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked EatenController.eatenDelete()");

        if (sessionCookie == null) {
            return "Error: Something as gone wrong.  Please contact the administrator with the error code EC-ED";
        }

        try {
            int userID = UserController.validateSessionCookie(sessionCookie);
            PreparedStatement statement = DatabaseConnection.newStatement(
                    "DELETE FROM Eaten WHERE EatenID = ?"
            );
            statement.setInt(1, eatenID);
            statement.executeUpdate();
            return "OK";

        } catch (Exception e) {
            System.out.println(e.getMessage());   //print the exception error message so it can be used to rectify the problem.  Do not send this back to client!
            return "Error: Something as gone wrong.  Please contact the administrator with the error code EC-ED. ";

        }
    }


    @POST
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public String eatenAdd(@FormParam("mealName") String mealName,
                           @FormParam("serving") double serving,
                           @FormParam("dateEaten") String dateEaten,
                           @FormParam("foodID") String foodID,
                           @CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked EatenController.eatenAdd()");

        if (sessionCookie == null) {
            return "Error: Something as gone wrong.  Please contact the administrator with the error code EC-ED";
        }

        try {
            int userID = UserController.validateSessionCookie(sessionCookie);
            PreparedStatement statement = DatabaseConnection.newStatement(
                    "INSERT INTO Eaten (MealName, Serving, DateEaten, FoodID, UserID) VALUES ( ?, ?, ?, ?, ?)"
            );
            statement.setString(1, mealName);
            statement.setDouble(2, serving);
            statement.setString(3, dateEaten);
            statement.setString(4, foodID);
            statement.setInt(5, userID);
            statement.executeUpdate();
            return "OK";
        } catch (Exception e) {
            System.out.println(e.getMessage());   //print the exception error message so it can be used to rectify the problem.  Do not send this back to client!
            return "Error: Something as gone wrong.  Please contact the administrator with the error code EC-EA. ";
        }

    }


}

