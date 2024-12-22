#!/bin/bash
set -e
set -x

minikube kubectl -- port-forward service/kafka 9092:9092 &
minikube kubectl -- port-forward service/kafka-ui 8081:8080 &
minikube kubectl -- port-forward service/ms-area-write-db 27018:27018 &
minikube kubectl -- port-forward service/ms-area-read-db 5433:5433 &
minikube kubectl -- port-forward service/ms-area-redis-db 6380:6380 &
minikube kubectl -- port-forward service/ms-creature-write-db 27017:27017 &
minikube kubectl -- port-forward service/ms-creature-read-db 5432:5432 &
minikube kubectl -- port-forward service/ms-creature-redis-db 6379:6379



