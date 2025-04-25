#!/bin/bash

# Check if gum is already installed
if ! command gum --version &> /dev/null; then
  echo "Gum not found. Installing gum..."
  sleep 1
  sudo apt update

  ## Switch to temp dir for the setup
  rm -rf /tmp/ttp-setup-tools
  mkdir -p /tmp/ttp-setup-tools
  cd /tmp/ttp-setup-tools

  # To make this script pretty installing Gum
  mkdir -p /etc/apt/keyrings
  curl -fsSL https://repo.charm.sh/apt/gpg.key | gpg --dearmor -o /etc/apt/keyrings/charm.gpg
  echo "deb [signed-by=/etc/apt/keyrings/charm.gpg] https://repo.charm.sh/apt/ * *" | tee /etc/apt/sources.list.d/charm.list
  sudo apt update && sudo apt install gum
else
  echo "Gum is already installed. Version: $(gum --version)"
  sleep 1
fi

# Clear the terminal
clear

# ASCII Art header
echo "█▀ ▀█▀ █▀█ █▀▀ █▀▀ ▀█▀   █▀█ █▀ █▀▀ █ █" | gum style --foreground 212 --align center --width 70 --margin "1 0 0 0"
echo "▄█ ░█░ █▀▄ ██▄ ██▄ ░█░   █▀█ ▄█ █▄▄ █ █" | gum style --foreground 212 --align center --width 70

# Get Mapillary API Key
echo "Enter your Mapillary API Key:" | gum style --padding "1 2" --width 50
while [ -z "$mapillaryKey" ] || ! echo "$mapillaryKey" | grep -qE "^MLY\|[a-zA-Z0-9_-]{16}\|[a-zA-Z0-9_-]{32}$"; do
  # Clear previous key if invalid format
  if [ -n "$mapillaryKey" ] && ! echo "$mapillaryKey" | grep -qE "^MLY\|[a-zA-Z0-9_-]{16}\|[a-zA-Z0-9_-]{32}$"; then
    # Clear screen and re-display prompt before showing error
    clear
    echo "Enter your Mapillary API Key:" | gum style --padding "1 2" --width 50
    echo "Invalid API key format! Please try again." | gum style --foreground 196
    mapillaryKey=""
  fi

  # Prompt for input
  mapillaryKey=$(gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter the Mapillary API key (press [ENTER] for hint)")

  # Show hint if empty
  if [ -z "$mapillaryKey" ]; then
    # Clear screen and re-display prompt before showing hint
    clear
    echo "Enter your Mapillary API Key:" | gum style --padding "1 2" --width 50
    echo "Expected format: MLY|****************|********************************" | gum style --foreground 208
  fi
done
clear

if gum confirm "Do you want to input an Imgur API key?"; then
  echo "Enter your Imgur API Key:" | gum style --padding "1 2" --width 50
  imgurKey=$(gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter your Imgur API key here")
else
  imgurKey="Disabled"
fi
clear

if gum confirm "Do you want to use geocoding features (search the map by addresses)?"; then
  echo "Enter your TravelTime app ID:" | gum style --padding "1 2" --width 50
  travelTimeAppId=$(gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter your TravelTime app ID here")
  clear
  echo "Enter your TravelTime API Key:" | gum style --padding "1 2" --width 50
  travelTimeKey=$(gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter your TravelTime API key here")
else
  travelTimeAppId="Disabled"
  travelTimeKey="Disabled"
fi
clear

echo "Select navigation mode:" | gum style --padding "1 2" --width 50
  navigation=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
    "Sequence navigation" \
    "Proximity navigation")
  clear

# Algorithm selection
# First, show a help screen option
echo "Do you want to see algorithm details before selecting?" | gum style --padding "1 2" --width 50
if gum confirm "Show algorithm details?"; then
  clear
  echo "ALGORITHM DETAILS:" | gum style --padding "1 2" --width 50 --foreground 212
  echo "Luminance 🌓: Converts image to grayscale based on pixel brightness" | gum style --margin "0 2 1 2"
  echo "Edge detection Sobel 📐: Highlights borders between contrasting areas using Sobel method" | gum style --margin "0 2 1 2"
  echo "Edge detection Sobel 📐: Highlights borders between contrasting areas using Canny method" | gum style --margin "0 2 1 2"
  echo "Braille ⠃⠗⠁⠊⠇⠇⠑: Represents the image using braille characters" | gum style --margin "0 2 1 2"
  echo "No algorithm : Represents the image using █ characters" | gum style --margin "0 2 1 2"
  echo "Press Enter to continue..." | gum style --foreground 240
  read -r
  clear
fi
clear

# Then show the selection
echo "Select an image conversion algorithm:" | gum style --padding "1 2" --width 50
algorithm=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
  "Luminance" \
  "Edge detection Sobel" \
  "Edge detection Canny" \
  "Braille" \
  "No algorithm")
clear

# Charset selection - skip if Braille is selected
if [ "$algorithm" = "Braille" ]; then
  # Set charset to braille constant without asking
  charset="BraillePatterns"

  # Show a notification that charset was auto-selected
  echo "Braille algorithm selected - using braille charset automatically" | gum style --padding "1 2" --width 60 --foreground 212
  sleep 2.5
else
  # Only show charset selection for non-Braille algorithms
  echo "Select the ASCII charset:" | gum style --padding "1 2" --width 50
  charset=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
    "Default (.:-=+*#%@)" \
    "Blocks ( ░▒▓█)" \
    "BlocksExtended ( ·░▒▓▄▌▐▀█)" \
    "Extended ( .'\`^\\\",:;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$" \
    "Braille (⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿)")
fi
clear

charset=$(echo "$charset" | cut -d' ' -f1)

if [[ "$algorithm" == "Braille" ]]; then
  # Automatically set no colors for Braille
  colors=false
elif [[ "$algorithm" == "No algorithm" ]]; then
  # Automatically set colors for "No algorithm"
  colors=true
else
  # For any other algorithm, ask user to pick if they want colors
  if gum confirm "Do you want colors?"; then
    colors=true
  else
    colors=false
  fi
fi

if [[ "$colors" == "true" ]]; then
  echo "Select a color filter:" | gum style --padding "1 2" --width 50
  colorFilter=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
    "Contrast filter" \
    "Colorblind Tritanopia filter" \
    "Colorblind Protanopia filter" \
    "Colorblind Deuteranopia filter" \
    "No filter")
  clear
else
  colorFilter="No filter"
fi

# Down sampling rate
echo "Set the down sampling rate:" | gum style --padding "1 2" --width 50
downSampling=""
while [ -z "$downSampling" ] || ! [[ "$downSampling" =~ ^[0-9]+$ ]] || [ "$downSampling" -lt 1 ] || [ "$downSampling" -gt 20 ]; do
  # If previous input was invalid but not empty, show error message
  if [ -n "$downSampling" ]; then
    # Clear previous error messages
    clear
    echo "Set the down sampling rate:" | gum style --padding "1 2" --width 50

    # Show appropriate error
    if ! [[ "$downSampling" =~ ^[0-9]+$ ]]; then
      echo "Invalid input! Please enter a number." | gum style --foreground 196
    elif [ "$downSampling" -lt 1 ] || [ "$downSampling" -gt 20 ]; then
      echo "Value must be between 1 and 20." | gum style --foreground 196
    fi
    # Reset the value
    downSampling=""
  fi

  # Get input
  downSampling=$(gum input --placeholder.foreground 240 --width 60 --placeholder "Enter the down sampling rate (press [ENTER] for hint)")

  # Show hint if empty (with clear to prevent stacking)
  if [ -z "$downSampling" ]; then
    clear
    echo "Set the down sampling rate:" | gum style --padding "1 2" --width 50
    echo "Please enter a number between 1 and 20" | gum style --foreground 208
  fi
done
clear

# Summary of selections
echo "Configuration Summary:" | gum style --padding "1 2" --width 50
{
  # Create formatted summary text with Imgur key right after Mapillary key
  summary="$(echo "🔑 Mapillary API key:" | gum style --foreground 212) ${mapillaryKey:0:4}...${mapillaryKey: -1}"

  # Add Imgur API key information right after Mapillary key
  if [[ "$imgurKey" != "Disabled" ]]; then
    summary+="
$(echo "🔑 Imgur API key:" | gum style --foreground 212) ${imgurKey:0:1}...${imgurKey: -1}"
  fi

  if [[ "$travelTimeAppId" != "Disabled" ]]; then
      summary+="
$(echo "🔑 TravelTime app ID:" | gum style --foreground 212) ${imgurKey:0:1}...${travelTimeAppId: -1}"
    fi

  if [[ "$travelTimeKey" != "Disabled" ]]; then
      summary+="
$(echo "🔑 TravelTime API key:" | gum style --foreground 212) ${imgurKey:0:1}...${travelTimeKey: -1}"
    fi

  # Continue with the rest of the summary
  summary+="
$(echo "🗺️ Navigation mode:" | gum style --foreground 212) $navigation
$(echo "🧮 Algorithm:" | gum style --foreground 212) $algorithm
$(echo "🔤 Charset:" | gum style --foreground 212) ${charset:0:20}
$(echo "⚙️ Down sampling rate:" | gum style --foreground 212) $downSampling"

  # Add color information conditionally
  if [[ "$colors" == "true" ]]; then
    summary+="
$(echo "🎨 Colors:" | gum style --foreground 212) Enabled ($colorFilter)"
  else
    summary+="
$(echo "🎨 Colors:" | gum style --foreground 212) Disabled"
  fi

  # Display summary with styling
  echo "$summary" | gum style --margin "0 2" --border normal --width 60 --padding "1 2"
}

# Ask for config file location
echo "Where would you like to save the configuration file?" | gum style --padding "1 2" --width 60

configPath=""
while [ -z "$configPath" ] || ! [[ "$configPath" =~ \.conf$ ]]; do
  # Show error if path doesn't end with .conf (but not empty)
  if [ -n "$configPath" ] && ! [[ "$configPath" =~ \.conf$ ]]; then
    # Clear screen and re-display prompt before showing error
    clear
    echo "Where would you like to save the configuration file?" | gum style --padding "1 2" --width 60
    echo "File must have a .conf extension. Please try again." | gum style --foreground 196
    configPath=""
  fi

  # Get input
  configPath=$(gum input --placeholder.foreground 240 --width 60 --placeholder "./streetascii.conf")

  # Use default if empty
  if [ -z "$configPath" ]; then
    configPath="./streetascii.conf"
    break
  fi
done
clear

# Write config and show spinner
echo ""
gum confirm "Ready to proceed and save configuration?" && {
  # Create directory if it doesn't exist
  mkdir -p "$(dirname "$configPath")"

  # Write config in HOCON format directly (not in a subshell)
  gum spin --spinner dot --title "Saving configuration..." -- sleep 1

  # Create the HOCON file
  cat > "$configPath" << EOF
api {
  mapillary-key = "${mapillaryKey}"
  imgur-key = "${imgurKey}"
  travelTime-app-id = "${travelTimeAppId}"
  travelTime-key ="${travelTimeKey}"
}

processing {
  navigation-type = "${navigation}"
  algorithm = "${algorithm}"
  charset = "${charset}"
  down-sampling-rate = ${downSampling}
}

colors {
  color = ${colors}
  color-filter = "${colorFilter}"
}
EOF
}

# Display completion message
clear
echo "✨ Configuration setup complete! ✨" | gum style --foreground 212 --align center --width 50 --border double --padding "1 2"
echo "Configuration saved to: $configPath" | gum style --foreground 212 --align center --width 50 --padding "1 2"

# Option to view the config file
if gum confirm "Would you like to view the configuration file?"; then
  gum pager < "$configPath"
fi