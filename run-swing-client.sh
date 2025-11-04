#!/bin/bash

# Run script for Swing Client
# This ensures all dependencies (including Gson) are on the classpath

echo "Building project..."
mvn clean compile -q

echo "Building classpath..."
mvn dependency:build-classpath -DincludeScope=runtime -q -Dmdep.outputFile=target/classpath.txt

if [ ! -f target/classpath.txt ]; then
    echo "Error: Failed to build classpath"
    exit 1
fi

CLASSPATH="target/classes:$(cat target/classpath.txt)"

echo "Starting Swing Client..."
java -cp "$CLASSPATH" com.evoting.swingclient.EVotingClient
