package net.resultrite.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import java.sql.Timestamp;
import java.time.LocalDateTime;
//import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import jakarta.ws.rs.core.SecurityContext;
import net.resultrite.dto.QuizDetailsDTO;
import net.resultrite.dto.QuizFinalDTO;
import net.resultrite.dto.QuizOptionDTO;
import net.resultrite.dto.QuizQuestionDTO;
import net.resultrite.dto.SaveMessage;

@Path("/")
public class QuizDetailsResource {
    private static Logger logger = LoggerFactory.getLogger(QuizDetailsResource.class);

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed("user")
    @Path("get-quizdetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuizDetailsById(@QueryParam("quiz_id") String quiz_id, @Context SecurityContext context) {
        if (context.getUserPrincipal() == null) {
            logger.error("User unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        QuizDetails quiz = QuizDetails.findById(quiz_id);
        if (quiz.getOwner_email().equals(jwt.getName())){
            return Response.ok(quiz).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
    }
    @GET
    @RolesAllowed("user")
    @Path("get-all-quizDetails1")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllQuizes(@Context SecurityContext context) {
        if (context.getUserPrincipal() == null) {
            logger.error("User unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        QuizFinalDTO quizFinal = new QuizFinalDTO();
       // QuizQuestion[] quizQuestionArr;
        List<QuizQuestion> quizQuestionList = new ArrayList<>();
        List<QuizDetails> quizes = QuizDetails.listAll();
        ObjectMapper objectMapper = new ObjectMapper();
        for (QuizDetails quiz : quizes){
            List<QuizQuestion> questions = QuizQuestion.find("quiz_id = ?1 ORDER BY create_ts asc", quiz.quiz_id)
                    .list();
                    for (QuizQuestion quizQuestion : questions){
                        JsonNode quizOption = quizQuestion.getOptions();
                        QuizOptionDTO[] optionsArray = objectMapper.convertValue(quizQuestion.getOptions().get("options"), QuizOptionDTO[].class);
                        System.out.println("optionsArray--"+optionsArray);
                        quizQuestion.setOptions(quizOption);
                        quizQuestionList.add(quizQuestion);
                        
                    }   
                    quizFinal.setQuiz_id(quiz.getQuiz_id()); 
                    quizFinal.setTopic(quiz.getTitle());  
                    quizFinal.setQuizQuestionFinal(quizQuestionList);
        }
        return Response.ok(quizFinal).build();
    }
    
    @POST
    @RolesAllowed("user")
    @Path("create-quiz")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createQuiz(QuizDetailsDTO quizDetailsDTO, @Context SecurityContext context) {
        try {
            if (context.getUserPrincipal()==null){
                logger.error("User unauthorized");
               return Response.status(401, "Unauthozied").build();
            }
            long count=QuizDetails.findAll().count();
            count=count+1;
            QuizDetails quizDetails = new QuizDetails();
            quizDetails.setTitle(quizDetailsDTO.getTopic());
            String quizID=String.format("Q%06d",count);
            quizDetails.setQuiz_id(quizID);
            quizDetails.setStatus("OPEN");
            Timestamp ts =Timestamp.valueOf(LocalDateTime.now());
            quizDetails.setCreate_ts(ts);
            quizDetails.setUpdate_ts(ts);
            quizDetails.setOwner_email(jwt.getName());
            quizDetails.persist();
            logger.info("Quiz Created "+ quizDetails.getQuiz_id());
            int qid = 0;
            ObjectMapper objectMapper = new ObjectMapper();
            String[] defaultAns = {""};
            JsonNode answer = objectMapper.convertValue(defaultAns, JsonNode.class);

           // return Response.status(200, "Created quiz").build();
            for ( QuizQuestionDTO question : quizDetailsDTO.getQuestions()){
                qid++;
                QuizQuestion saveObject = new QuizQuestion();
                saveObject.setQuiz_id(quizID);
                String questionId = String.format("Q%03d",qid);
                saveObject.setQuestion_id(questionId);
                saveObject.setAnswer_type("SINGLE");
                List<QuizOptionDTO> newList = new ArrayList<QuizOptionDTO>();
                int optId=1;
                for (QuizOptionDTO opt:question.getOptions()){
                    opt.setOptId(String.format("%s_%d",questionId,optId));
                    optId++;
                    newList.add(opt);
                }
                question.setOptions(newList);
                question.setQuestion_id(questionId);
                JsonNode opts = objectMapper.convertValue(question, JsonNode.class);
                saveObject.setAnswer(answer);
                saveObject.setOptions(opts);
                saveObject.setCreate_ts(ts);
                saveObject.setUpdate_ts(ts);
                saveObject.persist();
                //saveObject.setOptions(null);
            }
            // Build the response DTO
            SaveMessage saveResponse = new SaveMessage();
            saveResponse.setMessage("Quiz Created successfully");
            saveResponse.setCode(200);
            // Serialize the response to JSON
            String jsonResponse = objectMapper.writeValueAsString(saveResponse);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();

        } catch (Exception ex) {
            logger.error("Error creating quiz", ex);
            return Response.serverError().build();
        }
    }

    @GET
    @RolesAllowed("user")
    @Path("get-all-quizDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllQuize(@Context SecurityContext context) {
        if (context.getUserPrincipal() == null) {
            logger.error("User unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        
        List<QuizFinalDTO> quizFinalDTOList = new ArrayList<>();
        List<QuizDetails> quizes = QuizDetails.listAll();
       
       
        for (QuizDetails quiz : quizes){

            System.out.println("--------------quiz"+quiz.quiz_id);
            
            List<QuizQuestion> questions = QuizQuestion.find("quiz_id = ?1 ORDER BY create_ts asc", quiz.quiz_id)
                    .list();
                    List<QuizQuestion> quizQuestionList = new ArrayList<>();
                    for (QuizQuestion quizQuestion : questions){
                        QuizQuestion singleQuizQuestion = convertToQuizQuestion(quizQuestion);
                        JsonNode quizOption = singleQuizQuestion.getOptions();
                       
                        singleQuizQuestion.setOptions(quizOption);
                        quizQuestionList.add(singleQuizQuestion);                                      
                    }   
                    QuizFinalDTO quizFinal = new QuizFinalDTO();
                    quizFinal.setQuiz_id(quiz.getQuiz_id());
                    quizFinal.setTopic(quiz.getTitle());
                    quizFinal.setQuizQuestionFinal(quizQuestionList);
                    quizFinalDTOList.add(quizFinal);

                    System.out.println("quiz.getQuiz_id()--"+quiz.getQuiz_id());
                    System.out.println("quiz.getTitle()--"+quiz.getTitle());
        }
       
        return Response.ok(quizFinalDTOList).build();
    }


    public QuizQuestion convertToQuizQuestion(QuizQuestion quizQuestion) {

        QuizQuestion quizQuestion1 = new QuizQuestion();
        quizQuestion1.setQuiz_id(quizQuestion.getQuiz_id());
        quizQuestion1.setQuestion_id(quizQuestion.getQuestion_id());
        quizQuestion1.setAnswer(quizQuestion.getAnswer());
        quizQuestion1.setAnswer_type(quizQuestion.getAnswer_type());
        quizQuestion1.setOptions(quizQuestion.getOptions());
        quizQuestion1.setScore(quizQuestion.getScore());
        quizQuestion1.setUpdate_ts(quizQuestion.getUpdate_ts());
        quizQuestion1.setCreate_ts(quizQuestion.getCreate_ts());
    

    return quizQuestion1;
}
}
