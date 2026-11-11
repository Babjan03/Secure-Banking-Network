@echo off
echo ========================================================
echo Compiling Secure and Reliable Banking Network Project...
echo ========================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -cp "lib/*" -d bin src\network\*.java src\routing\*.java src\security\*.java src\communication\*.java src\error\*.java src\reliability\*.java src\banking\*.java src\database\*.java src\logging\*.java src\ui\*.java src\main\*.java

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Compilation finished without errors.
) else (
    echo [ERROR] Compilation failed!
)
pause
