package github.vaporrrr.btediscordaddon.schematics;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Upload {
    private final Plugin plugin;
    public Upload(Plugin  plugin){
        this.plugin = plugin;
    }
    public void execute(DiscordGuildMessageReceivedEvent event){
        Plugin worldEdit = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
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
            if (attachments.size() == 1) {
                String fileName = attachments.get(0).getFileName();

                if (fileName.endsWith(".schematic") || fileName.endsWith(".schem")) {
                    File file = new File(schematicsFolder, fileName);
                    if (!file.exists()) {
                        if(file.length() <= 8388608){
                            attachments.get(0).downloadToFile(file);
                            event.getChannel().sendMessage(fileName + " has been successfully uploaded to the server.").queue();
                        } else {
                            errorMessage(event, "File is too large. (>8MB)");
                        }
                    } else {
                        errorMessage(event,fileName + " already exists.");
                    }
                } else {
                    errorMessage(event, "The attachment must be a .schematic or .schem file.");
                }
            } else {
                errorMessage(event, "The message must have a schematic attached to it.");
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
    public static void errorMessage(DiscordGuildMessageReceivedEvent event, String errorMessage) {
        event.getChannel().sendMessage(errorMessage).complete().delete().completeAfter(2, TimeUnit.SECONDS);
        try {
            event.getMessage().delete().queue();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
