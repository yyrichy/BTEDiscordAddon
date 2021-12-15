# DiscordPlus (1.12.2 - 1.17)
Add features through DiscordSRV API. Tested on 1.12.2 and 1.17, native 1.12.2

## Features:
- Server status message in Discord, edited with # of players and list of players
- Placeholders for Discord usernames, IDs, etc (Placeholder options: %username% %discord-mention% %discord-tag% %discord-username% %discord-id%)
- `(prefix)setup` sets status message
- Log Discord account link and unlink
- Link account (DiscordSRV) through channel instead of DMs
- Upload and download schematics through Discord channel (Majority of code from [RudeYeti](https://github.com/RudeYeti))

## WIP:
- `(prefix)linked` equivalent of `/discord linked`
- Statistics messages with Discord, Minecraft (LuckPerms as dependency), and BTE website stats (In develop branch)
- Rewrite of everything
- Custom made AFK system (Essentials AFK acts weirdly when a player first joins)

## Requires: 
- DiscordSRV
- WorldEdit
- LuckPerms
