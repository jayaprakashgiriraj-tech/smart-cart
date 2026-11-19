@echo off
title Smart Cart AI - Machine Learning Engine
echo ============================================================
echo Starting Smart Cart AI Machine Learning Engine (100% Local)
echo Hardware Profile: Optimized for 4 GB RAM (CPU Development)
echo No external AI APIs - Scikit-learn + FastAPI Service
echo ============================================================
set SCRIPT_DIR=%~dp0
set PYTHON_EXE=%SCRIPT_DIR%..\python311\python.exe

if not exist "%PYTHON_EXE%" (
    echo [ERROR] Python not found at %PYTHON_EXE%
    pause
    exit /b 1
)

cd /d "%SCRIPT_DIR%"
echo Server running at: http://127.0.0.1:8000
echo Swagger UI Docs:  http://127.0.0.1:8000/docs
echo.
"%PYTHON_EXE%" -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
pause
