# Instructions for lab session


1. Download the API code 
```
mkdir ~/projectes
cd projectes
git clone https://github.com/suddutt1/sit-workshop-api.git
cd sit-workshop-api
```
2. Compile the code . Run the following command from sit-workshop-api directory.

```
./mvnw package
```
3. Create a container . Run the following command from sit-workshop-api directory.  
```
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/quiz-api-jvm:1 .

```
4. Deploy and run the environment 

```
cd lab/docker
docker image pull postgres:16.1-bookworm
docker compose up 
```

5. Open the link [http://localhost:8080/q/swagger-ui/] in the a browser

6. If your browser does not show any thing run the following coammand
```
docker compose ps
docker compose restart api

```
