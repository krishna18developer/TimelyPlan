#!/bin/bash

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt "11" ]; then
    echo "Java 11 or higher is required. Current version: $java_version"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven."
    exit 1
fi

# Check if the JAR file exists when using --no-build
if [ "$1" = "--no-build" ]; then
    if [ ! -f "target/timelyplan-1.0-SNAPSHOT-jar-with-dependencies.jar" ]; then
        echo "JAR file not found. Please run without --no-build first to build the project."
        exit 1
    fi
    echo "Starting TimelyPlan..."
    java -jar target/timelyplan-1.0-SNAPSHOT-jar-with-dependencies.jar
else
    echo "Building TimelyPlan..."
    mvn clean package

    if [ $? -eq 0 ]; then
        echo "Build successful. Starting TimelyPlan..."
        java -jar target/timelyplan-1.0-SNAPSHOT-jar-with-dependencies.jar
    else
        echo "Build failed. Please check the error messages above."
        exit 1
    fi
fi 