# BTEDiscordAddon (1.12 - 1.18)
Add features through DiscordSRV API. Tested on 1.12.2 and 1.18.1, native 1.12.2

## Features:
- Server status message in Discord, edited with # of players and list of players
- Placeholders for Discord usernames, IDs, etc. (Placeholder options: %username% %discord-mention% %discord-tag% %discord-username% %discord-id%)
- `(prefix)setup` sets status message
- `(prefix)online` lists online players
- `(prefix)linked` equivalent of `/discord linked`
- Custom-made AFK system
- Statistics messages with Discord, Minecraft (Uses LuckPerms for group stats), and BTE website stats
- Link MC <-> Discord account (DiscordSRV) through channel instead of DMs
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

    $unix$ $guild_age_unix$ $guild_members$ $guild_member_max$ $guild_categories$ $guild_channel_voice$ $guild_channel_text$
    $guild_channel_store\$ $guild_channels$ $guild_roles$ $guild_emotes$ $guild_emote_max$ $guild_boosts$ $guild_boosters$

    $bte_project_locations$
    (Following requires BTE website API key from https://github.com/BuildTheEarth/build-team-api): 
    $bte_team_locations$ $bte_team_applications_pending$ $bte_team_members$ $bte_team_leaders$ $bte_team_co-leaders$ $bte_team_reviewers$ $bte_team_builders$ $bte_team_leader_list$ 
    $bte_team_co-leader_list$ $bte_team_reviewer_list$ $bte_team_builder_list$
</details>

## WIP:
- MC command that lists players and their afk status

## Requires: 
- DiscordSRV

## Soft Depend (Optional):
- PlaceholderAPI
- LuckPerms (For getting group size)
- WorldEdit (For schematic download and upload channels)
