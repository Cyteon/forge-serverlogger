# forge-serverlogger

forge-serverlogger is a Forge mod for Minecraft 1.18.2 that sends notifications to a Discord server for various in-game events, including:

- Server start/stop
- Player join/leave
- Player death
- Player advancements
- Chat messages
- When a player finds netherite

## Installation

1. Download the mod file from the [releases page](https://github.com/Cyteon/forge-serverlogger/releases).
2. Place the downloaded JAR file in the `mods` folder of your Minecraft Server.
3. Start the Minecraft Server.

## Configuration

Upon first launch, the mod will create a configuration file named `forge-serverlogger.toml` in the `config` folder of your Minecraft instance. Open this file and configure the following settings:

```toml
[Discord]
# Discord webhook URL for server logs
"Logs Webhook URL" = "YOUR_LOGS_WEBHOOK_URL_HERE"

# Discord webhook URL for chat messages
"Chat Webhook URL" = "YOUR_CHAT_WEBHOOK_URL_HERE"
```
Replace YOUR_LOGS_WEBHOOK_URL_HERE with the Discord webhook URL you want to use for server events. Replace YOUR_CHAT_WEBHOOK_URL_HERE with the Discord webhook URL you want to use for chat messages.

Usage
Once the mod is installed and configured, it will automatically send notifications to the specified Discord webhooks for the events listed above.

Building from Source
If you want to build the mod from source, you'll need to have the following tools installed:

- Java Development Kit (JDK) 17 or later
- Gradle

1. Clone the repository: `git clone https://github.com/Cyteon/forge-serverlogger.git`
2. Navigate to the project directory: `cd forge-serverlogger`
3. Build the mod: `./gradlew build`
The built JAR file will be located in the build/libs directory.

Contributing
Contributions are welcome! Please open an issue or submit a pull request on the GitHub repository.
