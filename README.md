# CustomWhitelistV2

CustomWhitelistV2 is a Minecraft plugin developed in Java using the Bukkit API. It provides a custom whitelist functionality with a variety of commands for server administrators and moderators.

## Requirements

- Server: [PaperMC](https://papermc.io/)
- Permission System: [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/)

## Features

- Add or remove players from the whitelist.
- Update the status of a player (e.g., WHITELISTED, NOT_WHITELISTED, BANNED, KICKED, TEMP_BANNED, TEMP_KICKED, REMOVED, UNKNOWN).
- Enable or disable specific sub-commands.
- Check the status of a player.
- Update and check the join password.
- Login functionality.
- Send messages to all moderators.
- Manage the plugin with a locally hosted web server (open on Port 7777)
- Change the language of the plugin (work in progress)

## Commands

- `/customWhitelistV2 <enableOrDisableASubCommand|listAllActivatedSubCommands|addPlayer|removePlayer|listPlayers|statusOfPlayer|updatePlayerStatus|updatePassword|checkPassword|help>`
- `/login <password>`
- `/msgModerator <message>`
- `/customWhitelistV2Admin <debug|addModerator|removeModerator|listAllModerators|setPluginsLanguage>`

## Installation

1. Download the latest release from the GitHub repository.
2. Place the .jar file in your server's plugins folder.
3. Restart your server.
4. Give yourself the Luck Perms role "customwhitelistv2.administrator" with the command `/lp user <user> permissions set customwhitelistv2.administrator`

## Contributing

Bug reports and feature requests are welcome. Please open an issue on GitHub if you encounter a problem or have a suggestion.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Contact

GitHub: [Shotix](https://github.com/Shotix)
