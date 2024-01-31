#!/bin/bash
echo "Pulling DB docker image"
docker image pull postgres:16.1-bookworm
echo "Creating the cluster"
kind create cluster --config=./kind-cluster.yaml --name=app-cluster
sleep 5 
echo "Pushing the images into cluster"
kind load docker-image postgres:16.1-bookworm postgres:16.1-bookworm  --name=app-cluster
kind load docker-image quarkus/quiz-api-jvm:1 quarkus/quiz-api-jvm:1 --name=app-cluster
echo "Creating namespace"
kubectl create ns quiz
echo "Deploying db"
echo "Creating volume"
kubectl apply -f ./pg-pv.yaml -n quiz
sleep 5
echo "Creating volume claim"
kubectl apply -f ./pg-pvc.yaml -n quiz
sleep 5
echo "Creating configuration"
kubectl create configmap pginit-config --from-file=./init.sql -n quiz
echo "Creating DB Pods "
kubectl apply -f ./db-deployment.yaml -n quiz
kubectl wait --namespace quiz \
  --for=condition=ready pod \
  --selector=app:pgdb \
  --timeout=90s
echo "Create service"
kubectl apply -f ./db-service.yaml -n quiz

