# BTEDiscordAddon (1.12 - 1.18)
Add features through DiscordSRV API. Tested on 1.12.2 and 1.18.1, native 1.12.2

## Features:
- Server status message in Discord, edited with # of players and list of players
- Placeholders for Discord usernames, IDs, etc. (Placeholder options: %username% %discord-mention% %discord-tag% %discord-username% %discord-id%)
- `(prefix)setup` sets status message
- `(prefix)online` lists online players
- Log Discord account link and unlink
- Link account (DiscordSRV) through channel instead of DMs
- Upload and download schematics through Discord channel (Majority of code from [RudeYeti](https://github.com/RudeYeti))
- PlaceholderAPI support in Server Status message and Stats messages

Default Placeholders:
<details>
    <summary>Server Status</summary>
    $player_name$ $player_name_with_afk_status$ $discord_mention$ $discord_tag$ $discord_username> $discord_id$
</details>
<details>
    <summary>Minecraft Stats</summary>
    $unix$ $unique_players_joined$ $linked_players$ $memory$ $uptime$
</details>
<details>
    <summary>Team Stats</summary>
    $unix$ $guild_age_unix$ $guild_member_count$ $guild_category_count$ $guild_channel_voice_count$ $guild_channel_text_count$
    $guild_channel_store_count$ $guild_channel_count$ $guild_role_count$ $guild_emote_count$ $guild_boost_count$ $guild_booster_count$
</details>

## WIP:
- `(prefix)linked` equivalent of `/discord linked` (needs to be remade)
- Statistics messages with Discord, Minecraft (Uses LuckPerms for group stats), and BTE website stats
- Rewrite (Partly complete)
- Custom-made AFK system (Essentials AFK acts weirdly when a player first joins) (Mostly complete)

## Requires: 
- DiscordSRV

## Soft Depend (Optional):
- PlaceholderAPI
- LuckPerms (For getting group size)
- WorldEdit (For schematic download and upload channels)
