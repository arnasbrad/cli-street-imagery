#!/bin/sh

# Clear the terminal
clear

# ASCII Art header
echo "█▀ ▀█▀ █▀█ █▀▀ █▀▀ ▀█▀   █▀█ █▀ █▀▀ █ █" | gum style --foreground 212 --align center --width 70 --margin "1 0 0 0"
echo "▄█ ░█░ █▀▄ ██▄ ██▄ ░█░   █▀█ ▄█ █▄▄ █ █" | gum style --foreground 212 --align center --width 70

# Get Mapillary API Key
echo "Enter your Mapillary API Key:" | gum style --padding "1 2" --width 50
while [ -z "$mapillaryKey" ] || ! echo "$mapillaryKey" | grep -qE "^MLY\|[a-zA-Z0-9_-]{16}\|[a-zA-Z0-9_-]{32}$"; do
  # Clear previous key if invalid format
  if [ ! -z "$mapillaryKey" ] && ! echo "$mapillaryKey" | grep -qE "^MLY\|[a-zA-Z0-9_-]{16}\|[a-zA-Z0-9_-]{32}$"; then
    echo "Invalid API key format! Please try again." | gum style --foreground 196
    mapillaryKey=""
  fi

  # Prompt for input
  mapillaryKey=$(gum input --placeholder.foreground 240 --width 50 --password --placeholder "Enter the Mapillary API key (press [ENTER] for hint)")

  # Show hint if empty
  if [ -z "$mapillaryKey" ]; then
    echo "Expected format: MLY|****************|********************************" | gum style --foreground 208
  fi
done

clear

# Algorithm selection
# First, show a help screen option
echo "Do you want to see algorithm details before selecting?" | gum style --padding "1 2" --width 50
if gum confirm "Show algorithm details?"; then
  clear
  echo "ALGORITHM DETAILS:" | gum style --padding "1 2" --width 50 --foreground 212
  echo "Luminance 🌓: Converts image to grayscale based on pixel brightness" | gum style --margin "0 2 1 2"
  echo "Edge detection 📐: Highlights borders between contrasting areas" | gum style --margin "0 2 1 2"
  echo "Braille ⠃⠗⠁⠊⠇⠇⠑: Represents the image using braille characters" | gum style --margin "0 2 1 2"
  echo "Press Enter to continue..." | gum style --foreground 240
  read
  clear
fi

clear

# Then show the selection
echo "Select an image conversion algorithm:" | gum style --padding "1 2" --width 50
algorithm=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
  "Luminance" \
  "Edge detection" \
  "Braille")
clear

# Charset selection
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
    "Extended (\\.'\`^\\\",;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$)" \
    "Braille (⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿)")
fi
clear

charset=$(echo "$charset" | cut -d' ' -f1)

# Down sampling rate
echo "Set the down sampling rate:" | gum style --padding "1 2" --width 50
while [ -z "$downSampling" ]; do
  downSampling=$(gum input --placeholder.foreground 240 --width 50 --placeholder "Enter the down sampling rate (press [ENTER] for hint)")
  if [ -z "$downSampling" ]; then
    echo "Please enter a number (1-10 recommended)" | gum style --foreground 208
  fi
done
clear

# Summary of selections
echo "Configuration Summary:" | gum style --padding "1 2" --width 50
{
  # Create formatted summary text
  summary="$(echo "🔑 API key:" | gum style --foreground 212) ${mapillaryKey:0:4}...${mapillaryKey: -3}
$(echo "🧮 Algorithm:" | gum style --foreground 212) $algorithm
$(echo "🔤 Charset:" | gum style --foreground 212) ${charset:0:20}
$(echo "⚙️ Down sampling rate:" | gum style --foreground 212) $downSampling"

  # Display summary with styling
  echo "$summary" | gum style --margin "0 2" --border normal --width 60 --padding "1 2"
}

# Ask for config file location
echo "Where would you like to save the configuration file?" | gum style --padding "1 2" --width 60
configPath=$(gum input --placeholder.foreground 240 --width 60 --placeholder "./image_converter.conf")
# Use default if empty
if [ -z "$configPath" ]; then
  configPath="./image_converter.conf"
fi

# Write config and show spinner
gum confirm "Ready to proceed and save configuration?" && {
  # Create directory if it doesn't exist
  mkdir -p "$(dirname "$configPath")"

  # Write config in HOCON format directly (not in a subshell)
  gum spin --spinner dot --title "Saving configuration..." -- sleep 1

  # Create the HOCON file
  cat > "$configPath" << EOF
api {
  mapillary-key = "${mapillaryKey}"
}

processing {
  algorithm = "${algorithm}"
  charset = "${charset}"
  down-sampling-rate = ${downSampling}
}
EOF
}

# Display completion message
clear
echo "✨ Configuration setup complete! ✨" | gum style --foreground 212 --align center --width 50 --border double --padding "1 2"
echo "Configuration saved to: $configPath" | gum style --foreground 212 --align center --width 50 --padding "1 2"

# Option to view the config file
if gum confirm "Would you like to view the configuration file?"; then
  gum pager < $configPath
fi