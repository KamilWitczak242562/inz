@echo off
setlocal enabledelayedexpansion

set folders=api_gateway recipe_management_service auth_service machine_service resource_service planning_service

for %%f in (%folders%) do (
    echo Building project in folder: %%f
    cd %%f
    call .\gradlew build
    if errorlevel 1 (
        echo Build failed in folder %%f
        exit /b 1
    )
    cd ..
)

echo All projects built successfully!
pause
