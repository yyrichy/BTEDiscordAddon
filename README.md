# BTEDiscordAddon (1.12 - 1.18)
Add features through DiscordSRV API. Tested on 1.12.2 and 1.18.1, native 1.12.2

## Requires:
- [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/)

## Soft Depend (Optional):
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [LuckPerms](https://luckperms.net/)
- [WorldEdit](https://dev.bukkit.org/projects/worldedit) (For schematic download and upload channels)

## Features:
- Server status message in Discord, edited with # of players and list of players
- Placeholders and PlaceholderAPI support for Server Status and Statistics messages
- `(prefix)setup` sets status message
- `(prefix)online` lists online players
- `(prefix)linked` equivalent of `/discord linked`
- MC command that lists players and their afk status (gets groups from LuckPerms)
- Custom-made AFK system (Can be disabled/enabled) (Permission bted.afkauto to go afk/unafk automatically)
- Statistics messages with Discord, Minecraft (Uses LuckPerms for group stats), and BTE website stats
- Link MC <-> Discord account (DiscordSRV) through channel instead of DMs
- Upload and download schematics through Discord channel (Majority of code from [RudeYeti](https://github.com/RudeYeti))
- PlaceholderAPI support in Server Status message and Stats messages

## Minecraft Commands:
### /afk
Description: Toggle player's afk status  
Permission: bted.command.afk
### /online
Description: Lists online players  
Permission: bted.command.online
### /bted-update
Description: Updates the Server Status and Stats embeds in Discord  
Permission: bted.admin.update
### /bted-reload
Description: Reloads config, updates Server Status and Stats embeds in Discord  
Permission: bted.admin.reload

## Discord Commands:
Permissions can be set by specifying Discord role IDs in the config.yml under `DiscordCommands.commandname.Permissions.Roles`. In the future permissions based on user IDs, Discord permissions, etc., may be added.
### /linked
Description: Equivalent to DiscordSRV's `/discord linked`. Gets player's linked account info from UUID, Discord ID, Minecraft player name, or Discord name.
### /online
Description: Lists online players
### /setup
Description: Sets up the Server Status embed

## Placeholders:
(PlaceholderAPI supported)

### Default Placeholders
Surround with "%" on each side. Example `%time_now_unix%`
<details>
    <summary>Minecraft Server</summary>

    bted_time_now_unix
    bted_unique_players_joined
    bted_linked_players
    bted_memory
    bted_uptime
</details>
<details>
    <summary>Minecraft Player</summary>

    bted_player_name
    bted_player_name_escape_markdown
    bted_player_name_display
    bted_player_UUID
    bted_player_afk_status

    (Below requires player to have Discord account linked)
    bted_player_discord_id
    bted_player_discord_name
    bted_player_discord_tag
    bted_player_discord_creation_unix
    bted_player_discord_creation_date
    bted_player_discord_join_unix
    bted_player_discord_join_date
    bted_player_discord_boost_unix
    bted_player_discord_boost_date
    bted_player_discord_mention
    bted_player_discord_name_effective
    bted_player_discord_nickname
    bted_player_discord_status
    bted_player_discord_game_name
    bted_player_discord_game_url
    bted_player_discord_role_id
    bted_player_discord_role_name
    bted_player_discord_role_mention
    bted_player_discord_role_color_hex
</details>
<details>
    <summary>Discord Guild</summary>

    bted_guild_name
    bted_guild_id
    bted_guild_decription
    bted_guild_creation_unix
    bted_guild_creation_date
    bted_guild_banner_id
    bted_guild_banner_url
    bted_guild_icon_id
    bted_guild_icon_url
    bted_guild_splash_id
    bted_guild_splash_url
    bted_guild_region_name
    bted_guild_region_emoji
    bted_guild_region_key
    bted_guild_vanity_code
    bted_guild_members
    bted_guild_member_max
    bted_guild_categories
    bted_guild_channel_voice
    bted_guild_channel_text
    bted_guild_channel_store
    bted_guild_channels
    bted_guild_roles
    bted_guild_emotes
    bted_guild_emote_max
    bted_guild_boosts
    bted_guild_boosters

    bted_guild_owner_id
    bted_guild_owner_name
    bted_guild_owner_tag
    bted_guild_owner_creation_unix
    bted_guild_owner_cration_date
    bted_guild_owner_join_unix
    bted_guild_owner_join_date
    bted_guild_owner_boost_unix
    bted_guild_owner_boost_date
    bted_guild_owner_mention
    bted_guild_owner_name_effective
    bted_guild_owner_nickname
    bted_guild_owner_status
    bted_guild_owner_game_name
    bted_guild_owner_game_url
</details>
<details>
    <summary>BuildTheEarth Website API</summary>

    bted_website_locations_total

    (Below require an API key: https://github.com/BuildTheEarth/build-team-api)
    bted_website_locations_team
    bted_website_applications_pending
    bted_website_members
    bted_website_leaders
    bted_website_co-leaders
    bted_website_reviewers
    bted_website_builders
    bted_website_leader_list
    bted_website_co-leader_list
    bted_website_reviewr_list
    bted_website_builder_list
    bted_website_member_list
</details>
