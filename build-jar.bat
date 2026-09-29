@echo off
setlocal
cd /d "%~dp0"
echo ParCool Fabric 26.1.2 builder
echo.
java -version
if errorlevel 1 (
  echo.
  echo ERROR: Java was not found. Install JDK 25, then run this file again.
  exit /b 1
)
echo.
call gradlew.bat clean build --no-daemon
if errorlevel 1 exit /b %errorlevel%
echo.
echo SUCCESS. Open build\libs and use parcool-fabric-1.0.0.jar
explorer build\libs
