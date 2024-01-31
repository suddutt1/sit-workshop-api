# Instructions for lab session


1. Download the API code 
```
mkdir ~/projectes
cd projectes
https://github.com/suddutt1/sit-workshop-api.git
cd sit-workshop-api
```
2. Compile the code 

```
./mvnw package
```
3. Create a container  
```
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/quiz-api-jvm:1 .

```