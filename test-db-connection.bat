@echo off
echo === Testing Database Connection ===
echo.

cd src
javac -cp ".;../lib/*" application/DatabaseTest.java application/DatabaseConnection.java
if %errorlevel% equ 0 (
    echo.
    echo Compilation successful. Running test...
    echo.
    java -cp ".;../lib/*" application.DatabaseTest
) else (
    echo.
    echo Compilation failed. Please check:
    echo 1. MySQL JDBC driver is in lib/ directory
    echo 2. Java is installed and in PATH
)

pause



