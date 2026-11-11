@echo off
echo ========================================================
echo Starting Secure and Reliable Banking Network...
echo ========================================================

java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" main.Main
if %ERRORLEVEL% neq 0 (
    pause
)
