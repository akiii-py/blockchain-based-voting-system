@echo off
REM Run script for Swing Client on Windows
REM This ensures all dependencies (including Gson) are on the classpath

echo Building project...
call mvn clean compile -q

echo Building classpath...
call mvn dependency:build-classpath -DincludeScope=runtime -q -Dmdep.outputFile=target\classpath.txt

if not exist target\classpath.txt (
    echo Error: Failed to build classpath
    exit /b 1
)

set /p CLASSPATH=<target\classpath.txt
set CLASSPATH=target\classes;%CLASSPATH%

echo Starting Swing Client...
java -cp "%CLASSPATH%" com.evoting.swingclient.EVotingClient
