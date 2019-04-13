package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class ServerStart {

    public static void main(String[] args) {

        DatabaseConnection.open("DietTracker.db");

        Server jettyServer = new Server(8081);

        //jersey configuration when using an embedded server
        ResourceConfig config = new ResourceConfig();
        config.register(MultiPartFeature.class); //need this to have multipart form data needed for images
        config.packages("server");      //the package (folder) where all controllers are


        //Jersey runs as a servlet that handles incoming requests
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));


        ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.addServlet(jerseyServlet, "/*");   ///scoped handler that responds to requests that match the configured context path in this case simply /
        servletContext.setContextPath("/");   //????

        //how to set the default page for the server????  works if you put http://localhost:8081/ but want to not have slash
        servletContext.setWelcomeFiles(new String[] { "login.html" });

        // tell the web server to use the servletContext to handle incoming HTTP requests
        jettyServer.setHandler(servletContext);

        try {
            jettyServer.start();
            System.out.println("Server successfully started.");
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
            DatabaseConnection.close();
        }
    }
}

