@echo off
setlocal EnableDelayedExpansion

:: Add UTF-8 support for emoji
chcp 65001 >nul

:: Clear the terminal
cls

:: Check if gum is installed by trying to get its version
gum --version >nul 2>&1

:: If gum is not installed, install it
if %ERRORLEVEL% NEQ 0 goto InstallGum
goto ContinueScript

:InstallGum
echo gum is not installed. Installing it now...
:: Install gum using winget
winget install charmbracelet.gum
echo.
echo Installation completed.
echo Note: You may need to restart your command prompt for the changes to take effect.
echo.
echo 1. Try to continue with the script without restarting
echo 2. Exit and restart the script after reopening command prompt
echo.
set /p choice="Enter your choice (1 or 2): "

if "%choice%"=="1" goto ContinueScript
echo.
echo Please restart your command prompt and run the script again.
pause
exit /b 0

:ContinueScript
:: Display version if installed and continue with script
for /f "tokens=*" %%a in ('gum --version 2^>nul') do echo %%a
echo Continuing with the script...
timeout /t 2 >nul

cls

:: ASCII Art header
gum style --foreground 212 --align center --width 70 --margin "1 0 0 0" "█▀ ▀█▀ █▀█ █▀▀ █▀▀ ▀█▀   █▀█ █▀ █▀▀ █ █"
gum style --foreground 212 --align center --width 70 "▄█ ░█░ █▀▄ ██▄ ██▄ ░█░   █▀█ ▄█ █▄▄ █ █"

:: Get Mapillary API Key
gum style --padding "1 2" --width 50 "Enter your Mapillary API Key:"
for /f "delims=" %%a in ('gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter the Mapillary API key"') do set "mapillaryKey=%%a"
cls

:: Ask for Imgur API key
gum confirm "Do you want to input an Imgur API key?"
if %ERRORLEVEL% EQU 0 (
    gum style --padding "1 2" --width 50 "Enter your Imgur API Key:"
    for /f "delims=" %%a in ('gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter your Imgur API key here"') do set "imgurKey=%%a"
) else (
    set "imgurKey=Disabled"
)
cls

:: Ask for TravelTime API details
gum confirm "Do you want to use geocoding features (search the map by addresses)?"
if %ERRORLEVEL% EQU 0 (
    gum style --padding "1 2" --width 50 "Enter your TravelTime app ID:"
    for /f "delims=" %%a in ('gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter your TravelTime app ID here"') do set "travelTimeAppId=%%a"
    cls
    gum style --padding "1 2" --width 50 "Enter your TravelTime API Key:"
    for /f "delims=" %%a in ('gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter your TravelTime API key here"') do set "travelTimeKey=%%a"
) else (
    set "travelTimeAppId=Disabled"
    set "travelTimeKey=Disabled"
)
cls

:: Select navigation mode - NO TEMP FILES
gum style --padding "1 2" --width 50 "Select navigation mode:"
for /f "delims=" %%a in ('gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 "Sequence navigation" "Proximity navigation"') do set "navigation=%%a"
cls

:: Algorithm selection with help option
gum style --padding "1 2" --width 50 "Do you want to see algorithm details before selecting?"
gum confirm "Show algorithm details?"
if %ERRORLEVEL% EQU 0 (
    cls
    gum style --padding "1 2" --width 50 --foreground 212 "ALGORITHM DETAILS:"
    gum style --margin "0 2 1 2" "Luminance: Converts image to grayscale based on pixel brightness"
    gum style --margin "0 2 1 2" "Edge detection Sobel: Highlights borders between contrasting areas using Sobel method"
    gum style --margin "0 2 1 2" "Edge detection Canny: Highlights borders between contrasting areas using Canny method"
    gum style --margin "0 2 1 2" "Braille: Represents the image using braille characters"
    gum style --margin "0 2 1 2" "No algorithm: Represents the image using block characters"
    gum style --foreground 240 "Press Enter to continue..."
    pause >nul
    cls
)
cls

:: Algorithm selection - NO TEMP FILES
gum style --padding "1 2" --width 50 "Select an image conversion algorithm:"
for /f "delims=" %%a in ('gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 "Luminance" "Edge detection Sobel" "Edge detection Canny" "Braille" "No algorithm"') do set "algorithm=%%a"
cls

:: Charset selection - skip if Braille is selected
if "%algorithm%"=="Braille" (
    set "charset=BraillePatterns"
    gum style --padding "1 2" --width 60 --foreground 212 "Braille algorithm selected - using braille charset automatically"
    timeout /t 3 >nul
) else (
    gum style --padding "1 2" --width 50 "Select the ASCII charset:"
    for /f "delims=" %%a in ('gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 "Default" "Blocks" "BlocksExtended" "Extended" "Braille"') do set "charset=%%a"
)
cls

:: === Color Selection Refactored with GOTO ===
set "colors=false"  :: Default to false
set "colorFilter=No filter" :: Default filter

:: Decide IF colors should be enabled
if /I "%algorithm%"=="Braille" goto SkipColorPrompt
if /I "%algorithm%"=="No algorithm" (
    set "colors=true"
    goto SkipColorPrompt
)

:: Ask only for other algorithms
cls
gum confirm "Do you want colors?"
if %ERRORLEVEL% EQU 0 (
    set "colors=true"
) else (
    set "colors=false"
)
cls

:SkipColorPrompt
:: Now, check if colors are true and prompt for filter if needed
if /I not "%colors%"=="true" goto SkipColorFilter

:: This code only runs if colors is true
gum style --padding "1 2" --width 50 "Select a color filter:"
for /f "delims=" %%a in ('gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 "Contrast filter" "Colorblind Tritanopia filter" "Colorblind Protanopia filter" "Colorblind Deuteranopia filter" "No filter"') do set "colorFilter=%%a"
cls

:SkipColorFilter
:: === End of Refactored Color Selection ===

:: Down sampling rate - NO TEMP FILES (No validation)
gum style --padding "1 2" --width 50 "Set the down sampling rate (1-20):"
for /f "delims=" %%a in ('gum input --placeholder.foreground 240 --width 60 --placeholder "Enter a number between 1 and 20"') do set "downSampling=%%a"
cls

:: Summary of selections
gum style --padding "1 2" --width 50 "Configuration Summary:"

:: Display summary (Use delayed expansion here as variables might have been set in loops/ifs)
echo.
echo Mapillary API key: !mapillaryKey:~0,4!...!!mapillaryKey:~-1!!

if not "!imgurKey!"=="Disabled" (
    echo Imgur API key: !imgurKey:~0,1!...!!imgurKey:~-1!!
)

if not "!travelTimeAppId!"=="Disabled" (
    echo TravelTime app ID: !travelTimeAppId:~0,1!...!!travelTimeAppId:~-1!!
)

if not "!travelTimeKey!"=="Disabled" (
    echo TravelTime API key: !travelTimeKey:~0,1!...!!travelTimeKey:~-1!!
)

echo Navigation mode: !navigation!
echo Algorithm: !algorithm!
echo Charset: !charset!
echo Down sampling rate: !downSampling!

:: --- Conditional Color Display using GOTO ---
if /I "!colors!"=="true" goto :DisplayColorsEnabled
:: If not true, display disabled and skip the enabled line
echo Colors: Disabled
goto :ColorDisplayDone

:DisplayColorsEnabled
echo Colors: Enabled (!colorFilter!)

:ColorDisplayDone
:: --- End Conditional Color Display ---


:: Ask for config file location - NO TEMP FILES (No validation)
gum style --padding "1 2" --width 60 "Where would you like to save the configuration file?"
for /f "delims=" %%a in ('gum input --placeholder.foreground 240 --width 60 --placeholder ".\streetascii.conf"') do set "configPath=%%a"
:: Use default if empty
if "!configPath!"=="" set "configPath=.\streetascii.conf"
cls


:: Write config confirmation and call subroutine
echo.
gum confirm "Ready to proceed and save configuration?"
if %ERRORLEVEL% EQU 0 (
    call :WriteConfigFile "!configPath!" WriteResult

    if "!WriteResult!"=="Success" (
        echo Configuration saved to: !configPath!
        timeout /t 2 >nul
    ) else (
        echo ERROR during configuration file write process. See previous messages.
        pause
        goto :eof
    )
) else (
    cls
    echo Configuration save cancelled.
    goto EndScript
)

:: Display completion message
cls
gum style --foreground 212 --align center --width 50 --border double --padding "1 2" "Configuration setup complete"
gum style --foreground 212 --align center --width 50 --padding "1 2" "Configuration saved to: !configPath!"

:: Option to view the config file
gum confirm "Would you like to view the configuration file?"
if %ERRORLEVEL% EQU 0 (
    type "!configPath!" | gum pager
)

goto EndScript

:: ================================================
:: Subroutine to write the configuration file
:: ================================================
:WriteConfigFile <FilePath> <ResultVar>
setlocal
set "TargetConfigPath=%~1"
set "Result=Fail" :: Default to failure
set "WriteError="

:: --- Create directory ---
for %%i in ("%TargetConfigPath%") do set "configDir=%%~dpi"
if not "%configDir%"=="" (
    if not exist "%configDir%" (
        mkdir "%configDir%" >nul 2>&1
        if errorlevel 1 (
            echo ERROR: Could not create directory "%configDir%"
            echo Please check permissions and path.
            set "WriteError=1"
        )
    )
)
if defined WriteError goto :WriteConfigFile_Cleanup

:: --- Use standard echo with parentheses and normal %var% expansion ---
(echo api {) > "%TargetConfigPath%" || set "WriteError=1"
(echo   mapillary-key = "%mapillaryKey%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo   imgur-client-id = "%imgurKey%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo   travelTime-app-id = "%travelTimeAppId%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo   travelTime-key = "%travelTimeKey%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo }) >> "%TargetConfigPath%" || set "WriteError=1"
(echo() >> "%TargetConfigPath%" || set "WriteError=1"  :: echo( for blank line
(echo processing {) >> "%TargetConfigPath%" || set "WriteError=1"
(echo   navigation-type = "%navigation%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo   algorithm = "%algorithm%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo   charset = "%charset%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo   down-sampling-rate = %downSampling%) >> "%TargetConfigPath%" || set "WriteError=1"
(echo }) >> "%TargetConfigPath%" || set "WriteError=1"
(echo() >> "%TargetConfigPath%" || set "WriteError=1"  :: echo( for blank line
(echo colors {) >> "%TargetConfigPath%" || set "WriteError=1"
(echo   color = %colors%) >> "%TargetConfigPath%" || set "WriteError=1"
(echo   color-filter = "%colorFilter%") >> "%TargetConfigPath%" || set "WriteError=1"
(echo }) >> "%TargetConfigPath%" || set "WriteError=1"
:: --- End of individual appends ---

:: Check if any write error occurred
if defined WriteError (
    echo ERROR: Failed to write one or more lines to "%TargetConfigPath%".
    echo Please check permissions, path, and variable contents.
    goto :WriteConfigFile_Cleanup
)

:: If we reach here, it was successful
set "Result=Success"

:WriteConfigFile_Cleanup
:: Return the result to the caller using the variable name passed as %2
endlocal & set "%~2=%Result%"
goto :eof
:: ================================================
:: End Subroutine
:: ================================================


:EndScript
endlocal
:eof