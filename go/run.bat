@echo off
echo Running Go Data Processing System...
echo.
go run .
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Program completed successfully!
) else (
    echo.
    echo Program failed!
    pause
)
