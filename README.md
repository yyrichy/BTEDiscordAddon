# BTEDiscordAddon (1.12.2 - 1.17)
Add features through DiscordSRV API. Tested on 1.12.2 and 1.17, native 1.12.2

## Features:
- Server status message in Discord, edited with # of players and list of players
- Placeholders for Discord usernames, IDs, etc. (Placeholder options: %username% %discord-mention% %discord-tag% %discord-username% %discord-id%)
- `(prefix)setup` sets status message
- Log Discord account link and unlink
- Link account (DiscordSRV) through channel instead of DMs
- Upload and download schematics through Discord channel (Majority of code from [RudeYeti](https://github.com/RudeYeti))
- PlaceholderAPI support in server status message
- Default placeholders in server status message: 

## WIP:
- `(prefix)linked` equivalent of `/discord linked` (works but isn't pretty)
- Statistics messages with Discord, Minecraft (Uses LuckPerms for group stats), and BTE website stats
- Rewrite and add more customization to statistics messages config 
- Rewrite (Partly complete)
- Custom-made AFK system (Essentials AFK acts weirdly when a player first joins) (Mostly complete)

## Requires: 
- DiscordSRV
- WorldEdit (Will be made soft depend)
- LuckPerms (Will be made soft depend)

## Soft Depend (Optional):
- PlaceholderAPI
