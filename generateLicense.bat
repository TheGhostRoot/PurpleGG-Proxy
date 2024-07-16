@echo off
setlocal EnableDelayedExpansion

set "file=./src/main/java/me/purplegg/proxy/ConsoleUtils.java"
set "line_number=226"
call :GenerateRandomString random_string
set "new_content=        String LicenseKey = \"!random_string!\";"
rem Use PowerShell to edit the file directly
powershell -Command "$content = Get-Content '%file%'; $content[%line_number%-1] = '%new_content%'; $content | Set-Content '%file%'"
echo Generated License Key !random_string!> liceseKey.txt
exit /b

:GenerateRandomString
set "chars=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
set "%~1="
for /L %%i in (1,1,16) do (
    set /a "rand=!random! %% 62"
    for %%j in (!rand!) do set "%~1=!%~1!!chars:~%%j,1!"
)
goto :eof