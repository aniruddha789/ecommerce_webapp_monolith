#!/bin/bash

# Define variables
JAR_FILE='/apps/urban_kicks/ecommerce_webapp_monolith/target/urban-kicks-backend-0.0.1-SNAPSHOT.jar'
LOG_FILE='/apps/urban_kicks/logs/spring.log'
ERROR_LOG_FILE='/apps/urban_kicks/logs/stderr.log'
SERVICE_NAME='urban-kicks-backend'
JAVA_CMD='java -Dspring.profiles.active=prod -jar'
SRC_ROOT='/apps/urban_kicks/ecommerce_webapp_monolith'

# Function to check if the service is running
is_service_running() {
  # Check the number of processes running with the JAR file name
  PROCESS_COUNT=$(ps -eaf | grep "$JAR_FILE" | grep -v grep | wc -l)
  [ "$PROCESS_COUNT" -gt 0 ]
}

# Stop the service if it's already running
if is_service_running; then
  echo "Service is running. Stopping it..."
  pkill -f "$JAR_FILE"
  sleep 5  # Give it a few seconds to stop
else
  echo "Service is not running."
fi

cd $SRC_ROOT

# Pull the latest changes from Git
if ! git pull; then
    echo "Error: Failed to pull the latest changes from Git."
    exit 1
fi

# Clean and install the project
if ! mvn clean install; then
    echo "Error: Failed to clean and install the project."
    exit 1
fi


# Start the service
echo "Starting the service..."
nohup $JAVA_CMD $JAR_FILE > $LOG_FILE 2> $ERROR_LOG_FILE &

# Check if the service started successfully
if is_service_running; then
  echo "Service started successfully."
else
  echo "Failed to start the service."
fi

