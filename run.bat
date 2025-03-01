@echo off
setlocal enabledelayedexpansion

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Java is not installed. Please install Java 11 or higher.
    exit /b 1
)

REM Check Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)
set JAVA_VERSION=!JAVA_VERSION:"=!
for /f "delims=. tokens=1" %%v in ("!JAVA_VERSION!") do (
    set JAVA_MAJOR=%%v
)
if !JAVA_MAJOR! LSS 11 (
    echo Java 11 or higher is required. Current version: !JAVA_MAJOR!
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Maven is not installed. Please install Maven.
    exit /b 1
)

echo Building TimelyPlan...
call mvn clean package

if errorlevel 1 (
    echo Build failed. Please check the error messages above.
    exit /b 1
) else (
    echo Build successful. Starting TimelyPlan...
    java -jar target\timelyplan-1.0-SNAPSHOT-jar-with-dependencies.jar
) 