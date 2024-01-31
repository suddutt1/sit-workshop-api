package net.resultrite.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import net.resultrite.dto.QuizOptionDTO;
import net.resultrite.dto.QuizQuestionDTO;
import net.resultrite.dto.SaveMessage;
import net.resultrite.dto.ScoreDTO;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/")
public class ScoreResource extends PanacheEntityBase{

    @Inject
    JsonWebToken jwt;

    final Logger logger = LoggerFactory.getLogger(ScoreResource.class);

    @POST
    @Path("save-score")
    @RolesAllowed("user")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveScore(ScoreDTO score, @Context SecurityContext context) {
        
        try {
            if (context.getUserPrincipal() == null) {
                logger.error("User unauthorized");
                return Response.status(404,"Unauthozied").build();
            }
            List<QuizQuestion> questions = QuizQuestion.find("quiz_id = ?1 ",score.getQuiz_id()).list();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,String> qaMap = new HashMap<>();
            for (QuizQuestion question:questions){
                logger.info("opt string "+question.getOptions().toString());
                QuizQuestionDTO questionDetails = objectMapper.readValue(question.getOptions().toString(),QuizQuestionDTO.class);
                for (QuizOptionDTO option:questionDetails.getOptions()){
                    if (option.isCorrect()){
                        qaMap.put(question.getQuestion_id(),option.getOptId());
                        logger.info("qaMa:::: "+qaMap);
                        break;
                    }
                }
            }
            int points=0;
            for (QuizQuestionDTO question: score.getQuestions()){
                String correctAnsOpt = qaMap.get(question.getQuestion_id());
                logger.info("correctAnsOpt:::: "+correctAnsOpt);
                for ( QuizOptionDTO opt:question.getOptions()){
                    logger.info("opt.getOptId():::: "+opt.getOptId());
                    logger.info("opt.isSelected():::: "+opt.isSelected());
                    if (opt.isSelected() && opt.getOptId().equals(correctAnsOpt)){
                        points=points+2;
                        break;
                    }
                }
            }
           //return  Response.ok().build();
            //logger.info("answers::::::::::" + score.getAnswers());
            long count=Score.findAll().count();
            count=count+1;
            String scoreId=String.format("S%03d",count);
            Score scoreEntity = new Score();
            String score_id = scoreId;
            // if (score_id.equals("")) {
            //     score_id = "S001";
            // }
            logger.info("score_id::::::::::" + score_id);
            scoreEntity.setQuiz_id(score.getQuiz_id());
            scoreEntity.setCreate_ts(Timestamp.valueOf(LocalDateTime.now()));
            scoreEntity.setUpdate_ts(Timestamp.valueOf(LocalDateTime.now()));
            scoreEntity.setScore(points);
            scoreEntity.setScore_id(score_id);
            JsonNode opts = objectMapper.convertValue(score, JsonNode.class);
            scoreEntity.setAnswers(opts);
            scoreEntity.setUser_id(context.getUserPrincipal().getName());
            scoreEntity.persist();
            

            SaveMessage saveResponse = new SaveMessage();
            saveResponse.setMessage("Score Saved successfully");
            saveResponse.setCode(200);
            String jsonResponse = objectMapper.writeValueAsString(saveResponse);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
             


        } catch (Exception ex) {
            logger.error("An error occurred while saving the score", ex);
            return Response.serverError().build();
        }

    }

    /**
     * @param quiz_id
     * @param user_id
     * @return
     */
    @GET
    @Path("participant-score")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParticipantScore(@QueryParam("quiz_id") String quiz_id, @QueryParam("user_id") String user_id,
            @Context SecurityContext context) {
        try {
            if (context.getUserPrincipal() == null) {
                logger.error("User unauthorized");
                return Response.status(401, "Unauthozied").build();
            }
            List<Score> scores = Score
                    .find("quiz_id = ?1 and user_id = ?2 ORDER BY create_ts desc limit 1", quiz_id, user_id).list();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(scores);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            logger.error("An error occurred while retrieving the score", ex);
            return Response.serverError().build();
        }

    }

    /**
     * To be used by the quiz owner to view the quick details
     * @param quiz_id
     * @return
     */
    @GET
    @Path("all-scores")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllParticipantScore(@QueryParam("quiz_id") String quiz_id,@Context SecurityContext context) {
        try{
            if (context.getUserPrincipal()==null){
                logger.error("User unauthorized");
                return Response.status(401, "Unauthozied").build();
            }
            List<Score> scores = Score.find("quiz_id = ?1  ORDER BY create_ts asc", quiz_id).list();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(scores);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        }catch(Exception ex){
            logger.error("An error occurred while retrieving the score", ex);
            return Response.serverError().build();
        }
       
        
    }

    private Integer pullQuestionList(JsonNode scoreAns) {
        // final Logger logger = LoggerFactory.getLogger(ScoreResource.class);
        System.out.println("JsonNode::::::::::" + scoreAns);
        int totalScore = 0;
        try {
            String scoreJsonString = scoreAns.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(scoreJsonString);

            Map<String, String> keyValueMap = new HashMap<>();

            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = jsonNode.get(fieldName);

                System.out.println("Key: " + fieldName);
                System.out.println("Values:");

                if (fieldValue.isArray()) {
                    for (JsonNode valueNode : fieldValue) {
                        System.out.println(" ======= " + valueNode.asText());
                        // keyValueMap.put(fieldName, fieldValue.get(0).asText());
                        // keyValueMap.put(fieldName, fieldValue.get(0).toString());
                        keyValueMap.put(fieldName, fieldValue.get(0).toString());
                    }
                } else {
                    System.out.println(" -------- " + fieldValue.asText());
                }

                // Separate key-value pairs
            }
            System.out.println("keyValueMap:::::::" + keyValueMap);

            List<QuizQuestion> searchMap = searchByMap(keyValueMap);
            System.out.println("searchMap:::::::" + searchMap);
            // Create a Map to store questionId and answer
            Map<String, String> questionAnswerMap = createQuestionAnswerMap(searchMap);

            totalScore = areMapsEqual(keyValueMap, questionAnswerMap);
            System.out.println("totalScore:::::::" + totalScore);

        } catch (Exception e) {
            logger.error("An error occurred while saving the score", e);
        }
        return totalScore;
    }

    public List<QuizQuestion> searchByMap(Map<String, String> keyValueMap) {
        List<String> questionIds = new ArrayList<>(keyValueMap.keySet());
        return findByQuestionIds(questionIds);
       
    }

    private static Map<String, String> createQuestionAnswerMap(List<QuizQuestion> searchMap) {
        Map<String, String> questionAnswerMap = new HashMap<>();

        for (QuizQuestion quizQuestion : searchMap) {

            String questionId = quizQuestion.getQuestion_id();
            JsonNode answer = quizQuestion.getAnswer();
            int score = quizQuestion.getScore();
            // quizQuestion.setScore(score);

            System.out.println("score:::::::" + score);

            questionAnswerMap.put(questionId, answer.toString());
        }

        return questionAnswerMap;
    }

    private static <K, V> int areMapsEqual(Map<K, V> map1, Map<K, V> map2) {
        // Check for null and size mismatch
        int count = 0;
        if (map1 == null || map2 == null || map1.size() != map2.size()) {
            System.out.println("map1:::>>>>>>>>::::" + map1);
            count = 0;
        }

        // Iterate over entries and compare values
        for (Map.Entry<K, V> entry : map1.entrySet()) {
            System.out.println("map1:::=========::::" + map1);
            K key = entry.getKey();
            V value1 = entry.getValue();
            V value2 = map2.get(key);

            String formattedValue1 = formatAsJsonArray1(value1);
            // String cleanedFormattedValue1 = cleanJsonString(formattedValue1);
            System.out.println("formattedValue1:::=========::::" + formattedValue1);
            // Check for null values and equality
            if (!Objects.equals(formattedValue1, value2)) {
                System.out.println("value1:::=========::::" + value1);
                System.out.println("value2:::=========::::" + value2);

                count = count + 0;
            } else {
                System.out.println("count:::within else=========::::" + count);
                count = count + 2;
            }
        }
        System.out.println("count:::=====last====::::" + count);
        return count;
    }

    // private static <V> String formatAsJsonArray(V value) {
    //     try {
    //         // Create an ObjectMapper from Jackson library
    //         ObjectMapper objectMapper = new ObjectMapper();

    //         return objectMapper.writeValueAsString(new Object[] { value });
    //     } catch (Exception e) {

    //         e.printStackTrace();
    //         return "";
    //     }
    // }

    // private static String cleanJsonString(String jsonString) {
    //     // Remove leading and trailing double quotes
    //     jsonString = jsonString.replaceAll("^\"|\"$", "");

    //     // Unescape double quotes
    //     jsonString = jsonString.replaceAll("\\\\\"", "\"");

    //     return jsonString;
    // }

    private static <V> String formatAsJsonArray1(V value) {
        // Create a JSON array string with value as the only element
        // return "[\"" + value + "\"]";
        return "[" + value + "]";

    }

    private String generateScoreId(String quiz_id) {

        List<Score> scoreList = Score.find("quiz_id = ?1 ORDER BY create_ts desc limit 1", quiz_id).list();

        for (Score score : scoreList) {

            String scoreId = score.getScore_id();
            // Split the string into two parts
            String part1 = scoreId.substring(0, 1); // Extract the first character
            String part2 = scoreId.substring(1); // Extract the rest of the string
            int intValue = Integer.parseInt(part2);

            logger.info("part1:::::", part1);
            logger.info("part2:::::", part2);

            int newScore_id = intValue + 1;
            String score_Id = Integer.toString(newScore_id);

            String finalScoreId = "S" + score_Id;

            return finalScoreId;
        }
        return "";

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
            int intValue = Integer.parseInt(part2);

            logger.info("part1::>>>:::", part1);
            logger.info("part2::>>>:::", part2);

            return intValue;
        }
        return null;
    }

    public List<QuizQuestion> findByQuestionIds(List<String> questionIds) {
        return list("from QuizQuestion where question_id in ?1", questionIds);
        
    }
   

}
