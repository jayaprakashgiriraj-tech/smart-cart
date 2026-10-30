@echo off
title SmartCart AI - USB Reverse Proxy
echo ========================================================
echo  SmartCart AI: Connecting Your Phone to XAMPP Backend
echo ========================================================
echo.
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:80
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Port 8080 is now active!
    echo Your phone is directly connected to XAMPP on your PC via USB.
    echo You can now tap Sign Up or Log In in the app.
) else (
    echo.
    echo [NOTICE] Please make sure:
    echo  1. Your phone is connected via USB cable.
    echo  2. USB Debugging is enabled on your phone.
)
echo.
pause

