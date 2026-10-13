@echo off
echo ================================================================================
echo  Compiling SmartFactory Industrial Production Analytics Platform...
echo ================================================================================

if not exist bin mkdir bin

javac -d bin -sourcepath src src/smartfactory/Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please ensure JDK 17+ is on your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [OK] Compilation successful.
echo.
echo ================================================================================
echo  Executing SmartFactory Analytics Platform...
echo ================================================================================
echo.

java -cp bin smartfactory.Main

pause
