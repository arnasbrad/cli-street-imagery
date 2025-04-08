#!/bin/sh

# Clear the terminal
clear
# Reset terminal and place cursor at top-left
echo -e "\033[H\033[2J"

# ASCII Art header
echo "
   █ █▄█ ▄▀█ █▀▀ █▀▀   █▀▀ █▀█ █▄░█ █░█ █▀▀ █▀█ ▀█▀ █▀▀ █▀█
   █ ░█░ █▀█ █▄█ ██▄   █▄▄ █▄█ █░▀█ ▀▄▀ ██▄ █▀▄ ░█░ ██▄ █▀▄
" | gum style --foreground 212 --align center --width 70 --margin "1 0"

# Get Mapillary API Key
echo "Enter your Mapillary API Key:" | gum style --padding "1 2" --width 50
while [ -z "$mapillaryKey" ]; do
  mapillaryKey=$(gum input --placeholder.foreground 240 --width 50 --placeholder "Enter the Mapillary API key (press [ENTER] for hint)")
  if [ -z "$mapillaryKey" ]; then
    echo "Expected format: MLY|*****************|**********************" | gum style --foreground 208
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

# Then show the selection
echo "Select an image conversion algorithm:" | gum style --padding "1 2" --width 50
algorithm=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
  "Luminance" \
  "Edge detection" \
  "Braille")
clear

# Charset selection
echo "Select the ASCII charset:" | gum style --padding "1 2" --width 50
charset=$(gum choose --cursor.foreground 212 --selected.foreground 212 --height 10 \
  "Default (.:-=+*#%@)" \
  "Extended (\\.'\`^\\\",;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$)" \
  "Braille (⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿)")
clear

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
  summary="$(echo "🔑 API key:" | gum style --foreground 212) ${mapillaryKey:0:10}...${mapillaryKey: -10}
$(echo "🧮 Algorithm:" | gum style --foreground 212) $algorithm
$(echo "🔤 Charset:" | gum style --foreground 212) ${charset:0:20}...
$(echo "⚙️ Down sampling rate:" | gum style --foreground 212) $downSampling"

  # Display summary with styling
  echo "$summary" | gum style --margin "0 2" --border normal --width 60 --padding "1 2"
}

# Confirm and proceed
gum confirm "Ready to proceed?" && gum spin --spinner dot --title "Processing..." -- sleep 2

# Display completion message
clear
echo "✨ Image conversion complete! ✨" | gum style --foreground 212 --align center --width 50 --border double --padding "1 2"