package net.resultrite.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.cli.Option;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import net.resultrite.dto.QuizOptionDTO;
import net.resultrite.dto.QuizQuestionDTO;
import net.resultrite.dto.SaveMessage;

@Path("/")
public class QuizQuestionResource {

    @Inject
    JsonWebToken jwt;

    final Logger logger = LoggerFactory.getLogger(QuizQuestionResource.class);

    @POST
    @Path("create-quiz-question")
    @RolesAllowed("user")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveQuizQuestion(QuizQuestion quizQuestion, @Context SecurityContext context) {

        try {
            if (context.getUserPrincipal() == null) {
                logger.error("User unauthorized");
                return Response.status(401, "Unauthozied").build();
            }
            // Validate owenership
            long count = QuizDetails
                    .find("quiz_id= ?1 and owner_email = ?2", quizQuestion.getQuiz_id(), jwt.getName()).count();
            if (count == 0) {
                logger.error("User unauthorized. Does not own the quiz");
                return Response.status(401, "Unauthozied").build();
            }
            logger.info("Quiz_option:::::::::: " + quizQuestion.getOptions());
            quizQuestion.setCreate_ts(Timestamp.valueOf(LocalDateTime.now()));
            quizQuestion.setUpdate_ts(Timestamp.valueOf(LocalDateTime.now()));
            logger.info("Quiz_id:::::::::: " + quizQuestion.getQuiz_id());
            Integer questionId = fetchByQuizId(quizQuestion.getQuiz_id());
            logger.info("questionId:::::::::: " + questionId);
            if (questionId != null) {
                logger.info("questionId:::::::::: " + questionId);
                int newQuestionId = questionId + 1;
                String stringValueQuestionId = Integer.toString(newQuestionId);
                String originalQuestionId = "Q" + stringValueQuestionId;

                quizQuestion.setScore(2);
                quizQuestion.setQuestion_id(originalQuestionId);

                quizQuestion.persist();

                SaveMessage saveResponse = new SaveMessage();
                saveResponse.setMessage("QuizQuestion Saved successfully");
                saveResponse.setCode(200);

                // Serialize the response to JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(saveResponse);
                return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
            } else {
                logger.info("questionId:::FirstTime::::::: " + questionId);
                int newQuestionId = 1;
                String stringValueQuestionId = Integer.toString(newQuestionId);
                String originalQuestionId = "Q" + stringValueQuestionId;

                quizQuestion.setScore(2);
                quizQuestion.setQuestion_id(originalQuestionId);

                quizQuestion.persist();

                SaveMessage saveResponse = new SaveMessage();
                saveResponse.setMessage("QuizQuestion Saved successfully");
                saveResponse.setCode(200);

                // Serialize the response to JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(saveResponse);
                return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
            }

        } catch (Exception ex) {
            logger.error("An error occurred while saving the questions of a quiz", ex);
            return Response.serverError().build();
        }
    }

    public Integer fetchByQuizId(String quiz_id) {
        // Fetch quiz questions by quiz_id and order by create_ts
        List<QuizQuestion> listQuizQuestion = QuizQuestion
                .find("quiz_id = ?1 ORDER BY create_ts desc limit 1", quiz_id).list();
        for (QuizQuestion quizQuestion : listQuizQuestion) {
            System.out.println("Question: " + quizQuestion.getQuestion_id());
            System.out.println("Answer: " + quizQuestion.getAnswer());

            String originalString = quizQuestion.getQuestion_id();
            // Split the string into two parts
            String part1 = originalString.substring(0, 1); // Extract the first character
            String part2 = originalString.substring(1); // Extract the rest of the string
            logger.info("part1 ----->" +part1);
            int intValue = Integer.parseInt(part2);

            return intValue;
        }
        return null;
    }

    /**
     * @param quizId
     * @return
     */
    @GET
    @Path("get-quiz-question")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestions(@QueryParam("quiz_id") String quizId, @Context SecurityContext context) {
        try {
            if (context.getUserPrincipal() == null) {
                logger.error("User unauthorized");
                return Response.status(401, "Unauthozied").build();
            }
            final List<QuizQuestion> questions = QuizQuestion.find("quiz_id = ?1 ORDER BY create_ts asc", quizId)
                    .list();
             ObjectMapper objectMapper = new ObjectMapper();
           
            for (QuizQuestion quizQuestion : questions){
                QuizOptionDTO[] optionsArray = objectMapper.convertValue(quizQuestion.getOptions().get("options"), QuizOptionDTO[].class);
                for (QuizOptionDTO singleQuizOptionDTO : optionsArray){
                    singleQuizOptionDTO.setCorrect(false);
                    singleQuizOptionDTO.setSelected(false);

                    JsonNode opts = objectMapper.convertValue(singleQuizOptionDTO, JsonNode.class);
                    quizQuestion.setOptions(opts);
                }
            }
            
            String jsonResponse = objectMapper.writeValueAsString(questions);
            logger.info("jsonResponse",jsonResponse);
            return Response.ok(jsonResponse).build();
        } catch (Exception ex) {
            logger.error("An error occurred while fetching quiz questions", ex);
            return Response.serverError().build();
        }
    }

  
}
