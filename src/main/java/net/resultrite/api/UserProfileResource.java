package net.resultrite.api;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.build.Jwt;

import java.util.HashMap;
import java.util.HashSet;
//import jakarta.ws.rs.core.Response;
import java.util.List;
import net.resultrite.dto.AuthRequest;
import net.resultrite.dto.SaveMessage;

import java.util.Arrays;
import java.time.Duration;

@Path("/")
public class UserProfileResource {
    private static Logger logger = LoggerFactory.getLogger(UserProfileResource.class);

    // @Inject
    // UserProfile userProfile;

    @GET
    @Path("get-users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context SecurityContext context) {
        if (context.getUserPrincipal() == null) {
            logger.error("User unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        List<UserProfile> allPersons = UserProfile.listAll();
        return Response.ok(allPersons).build();
    }

    @POST
    @Path("create-users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUsers(UserProfile profile) {
        try {
            profile.persist();
            logger.info("User profile created "+ profile.getEmail());
           // return Response.status(200, "Created user profile").build();

            // Build the response DTO
            SaveMessage saveResponse = new SaveMessage();
            saveResponse.setMessage("User Profile Saved successfully");
            saveResponse.setCode(200);

            // Serialize the response to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(saveResponse);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();

        } catch (Exception ex) {
            logger.error("Error creating user profile", ex);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(AuthRequest authRequest) {
        try {
            //Find out the user
            List<UserProfile> userList = UserProfile.find("email = ?1 ", authRequest.getEmail()).list();
            if (userList==null || userList.size()==0){
                return Response.status(404,"Unauthorized").build();
            }
            UserProfile user = userList.get(0);
            if (!user.getPassword().equals(authRequest.getPassword())){
                return Response.status(401,"Unauthorized").build();
            }
            HashMap<String,String> auth= new HashMap<String,String>();
            String token =Jwt.issuer("https://resultrite.com/issuer") 
             .subject(user.getEmail()) 
             .groups(new HashSet<>(Arrays.asList("user"))) 
             .expiresIn(Duration.ofMinutes(30))
             .claim(Claims.full_name.name(),user.getName()).sign();
            // Serialize the response to JSON
            auth.put("token", token);
            auth.put("email", user.getEmail());
            auth.put("name",user.getName());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(auth);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();

        }catch(Exception ex){
            logger.error("An error occurred while authenticating the user", ex);
            return Response.serverError().build();
        }

    }
}
