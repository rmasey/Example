package server.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.DatabaseConnection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static server.Convertor.convertToJSONArray;


@Path("food/")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)

public class FoodController {

    @GET
    @Path("list")
    public String foodList(@QueryParam("searchString") String searchString) {

        System.out.println("Invoked FoodController.foodList() with searchString = " + searchString);

        try {  //put all in try block, Java closes all connecstion when you exit the try block (ps, rs)
            PreparedStatement statement = DatabaseConnection.connection.prepareStatement(   //using public connection

                    "SELECT FoodID, ServingSize, FibrePerServing, FatPerServing, SugarsPerServing, CalsPerServing, CarbsPerServing, ProteinPerServing, SatFatPerServing, SaltPerServing FROM Foods where FoodID LIKE ?"
            );
            statement.setString(1, '%' + searchString.toLowerCase() + '%');  //% is wildcard so FoodID contains search string
            ResultSet resultSet = statement.executeQuery();

            JSONArray response = convertToJSONArray(resultSet);   //convert resultSet to JSONArray
            return response.toString();

        } catch (Exception e) {         //if there's an exception when doing the SQL query or converting to JSON print error
            System.out.println(e.getMessage());   //print the exception error message so it can be used to rectify the problem.  Do not send this back to client!
            JSONObject response = new JSONObject();
            response.put("Error:", "Something as gone wrong.  Please contact the administrator with the error code FC-FL. ");
            return response.toString();

        }

    }


}

