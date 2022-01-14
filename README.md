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

## Default Placeholders:
<details>
    <summary>Server Status</summary>

    $player_name$
    $player_name_with_afk_status$
    $discord_mention$
    $discord_tag$
    $discord_username$
    $discord_id$
</details>
<details>
    <summary>Minecraft Stats</summary>

    $current_unix$
    $unique_players_joined$
    $linked_players$
    $memory$
    $uptime$
</details>
<details>
    <summary>Team Stats</summary>

    $current_unix$
    $guild_age_unix$
    $guild_members$
    $guild_member_max$
    $guild_categories$
    $guild_channel_voice$
    $guild_channel_text$
    $guild_channel_store$
    $guild_channels$
    $guild_roles$
    $guild_emotes$
    $guild_emote_max$
    $guild_boosts$
    $guild_boosters$

    $bte_project_locations$
    (Following requires BTE website API key: https://github.com/BuildTheEarth/build-team-api)
    $bte_team_locations$
    $bte_team_applications_pending$
    $bte_team_members$
    $bte_team_leaders$
    $bte_team_co-leaders$
    $bte_team_reviewers$
    $bte_team_builders$
    $bte_team_leader_list$ 
    $bte_team_co-leader_list$
    $bte_team_reviewer_list$
    $bte_team_builder_list$
</details>
