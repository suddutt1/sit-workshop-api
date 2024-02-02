# Instructions for lab session


1. Download the API code 
```
mkdir ~/projects
cd projectes
git clone https://github.com/suddutt1/sit-workshop-api.git
cd sit-workshop-api
```

2. Generate the keys for signing 
```
cd src/main/resources
openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
cd ../../../
```

3. Compile and package the code . Run the following command from sit-workshop-api directory.

```
./mvnw package
```

4. Create a container . Run the following command from sit-workshop-api directory.  

```
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/quiz-api-jvm:1 .

```
5. Deploy and run the application containers 

```
cd lab/docker
docker image pull postgres:16.1-bookworm
docker compose up -d 
docker compose ps
docker compose restart api
# Optionally you can check the api logs 
docker logs api 
```

6. Open the link [http://localhost:8080/q/swagger-ui/] in the a browser

7. (Optional) If your browser does not show any thing run the following coammand
```
docker compose ps
docker compose restart api

```
8. Run the application

8.1 Create an application user. Use POST /create-users

8.2 Authenticate the user. Use POST  /authenticate . Collect the token and put in authorize dialong box

8.3 Create a quiz using the POST /create-quiz and with following data
```
{
  "topic": "GK",
  "questions": [
    {
      "question": "__ is captial of India",
      "options": [
        {
          "option": "Delhi",
          "correct": true
        },
        {
          "option": "Kolkata",
          "correct": false
        }
      ]
    },
    {
      "question": "__ is captial of West Bengal",
      "options": [
        {
          "option": "Delhi",
          "correct": false
        },
        {
          "option": "Kolkata",
          "correct": true
        }
      ]
    }
  ]
}
```

8.4 Retrived the saved quiz . GET /get-all-quizDetails and note down the quiz id , question id 

8.5 Call the save score using POST /save-score

```
{
  "quiz_id": "Q000001",
  "questions": [
    {
      "question_id": "Q001",
      "options": [
        {
          "optId": "Q001_1",
          "selected": true
        },
        {
          "optId": "Q001_2",
          "selected": false
        }
      ]
    },
    {
      "question_id": "Q002",
      "options": [
        {
          "optId": "Q002_1",
          "selected": false
        },
        {
          "optId": "Q002_2",
          "selected": true
        }
      ]
    }
  ]
}
```
8.6 Review the scores by calling GET /all-scores
