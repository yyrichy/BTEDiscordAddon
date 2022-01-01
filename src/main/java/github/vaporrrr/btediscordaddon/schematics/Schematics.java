package github.vaporrrr.btediscordaddon.schematics;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schematics {
    private final Plugin worldEdit;
    private final BTEDiscordAddon bteDiscordAddon;

    public Schematics(BTEDiscordAddon bteDiscordAddon) {
        this.worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.bteDiscordAddon = bteDiscordAddon;
    }

    public void upload(DiscordGuildMessageReceivedEvent event) {
        if (worldEdit == null) {
            bteDiscordAddon.getLogger().warning("WorldEdit is not installed, cannot upload schematics.");
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
            int maxFileSize = bteDiscordAddon.getConfig().getInt("Schematics.Upload.MaxFileSizeInKB");
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
            bteDiscordAddon.getLogger().warning("WorldEdit is not installed, cannot download schematics.");
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
                if (file.exists() && inBaseDirectory(schematicsFolder, file) && file.length() < 8388608) {
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
