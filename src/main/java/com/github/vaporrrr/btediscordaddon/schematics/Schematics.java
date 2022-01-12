/*
 * BTEDiscordAddon
 * Copyright 2022 (C) vaporrrr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.vaporrrr.btediscordaddon.schematics;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schematics {
    private final Plugin worldEdit;

    public Schematics() {
        this.worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

    public void upload(DiscordGuildMessageReceivedEvent event) {
        if (worldEdit == null) {
            BTEDiscordAddon.warn("WorldEdit is not installed, cannot upload schematics.");
            return;
        }
        File schematicsFolder = new File(worldEdit.getDataFolder() + File.separator + "schematics");
        try {
            boolean folderMade = true;
            if (!schematicsFolder.exists()) {
                folderMade = schematicsFolder.mkdir();
            }
            if (!folderMade) {
                errorMessage(event, "Schematics folder does not exist, and could not create one.");
                return;
            }
            List<Message.Attachment> attachments = event.getMessage().getAttachments();
            if (attachments.size() != 1) {
                errorMessage(event, "The message must have a schematic attached to it with no other files.");
                return;
            }
            String fileName = attachments.get(0).getFileName();
            if (!fileName.endsWith(".schematic") && !fileName.endsWith(".schem")) {
                errorMessage(event, "The attachment must be a .schematic or .schem file.");
                return;
            }
            File file = new File(schematicsFolder, fileName);
            if (file.exists()) {
                errorMessage(event, fileName + " already exists.");
                return;
            }
            int maxFileSize = BTEDiscordAddon.config().getInt("Schematics.Upload.MaxFileSizeInKB");
            if (file.length() > maxFileSize * 1024L) {
                errorMessage(event, "File size is greater than " + maxFileSize + " KB.");
                return;
            }
            attachments.get(0).downloadToFile(file);
            event.getChannel().sendMessage(fileName + " has been successfully uploaded to the server.").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(DiscordGuildMessageReceivedEvent event) {
        if (worldEdit == null) {
            BTEDiscordAddon.warn("WorldEdit is not installed, cannot download schematics.");
            return;
        }
        File schematicsFolder = new File(worldEdit.getDataFolder() + File.separator + "schematics");
        String name = event.getMessage().getContentRaw();
        try {
            if (name.isEmpty()) {
                errorMessage(event, "Name a schematic to download.");
                return;
            }
            if (!schematicsFolder.exists()) {
                errorMessage(event, "The schematics folder does not exist.");
                return;
            }
            ArrayList<File> potentialFiles = new ArrayList<>();
            potentialFiles.add(new File(schematicsFolder, name + ".schematic"));
            potentialFiles.add(new File(schematicsFolder, name + ".schem"));
            ArrayList<File> files = new ArrayList<>();
            for (File file : potentialFiles) {
                if (file.exists() && inBaseDirectory(schematicsFolder, file) && file.length() < Message.MAX_FILE_SIZE) {
                    files.add(file);
                }
            }
            if (files.size() == 0) {
                errorMessage(event, "No schematic of that name found.");
            } else {
                for (File file : files) {
                    event.getChannel().sendFile(file).queue();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void errorMessage(DiscordGuildMessageReceivedEvent event, String errorMessage) {
        event.getChannel().sendMessage(errorMessage).complete().delete().completeAfter(2, TimeUnit.SECONDS);
        try {
            event.getMessage().delete().queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean inBaseDirectory(File base, File user) {
        URI parentURI = base.toURI();
        URI childURI = user.toURI();
        return !parentURI.relativize(childURI).isAbsolute();
    }
}
