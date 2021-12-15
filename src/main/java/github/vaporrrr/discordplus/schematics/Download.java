package github.vaporrrr.discordplus.schematics;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Download {
    private final Plugin plugin;
    public Download(Plugin  plugin){
        this.plugin = plugin;
    }
    public void execute(DiscordGuildMessageReceivedEvent event){
        Plugin worldEdit = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        File schematicsFolder = new File(worldEdit.getDataFolder() + File.separator + "schematics");
        String name = event.getMessage().getContentRaw();
        try {
            if(!schematicsFolder.exists()){
                errorMessage(event, "The schematics folder does not exist.");
            } else if (name.isEmpty()) {
                errorMessage(event, "Name a schematic to download.");
            } else {
                File file = new File(schematicsFolder, name + ".schematic");
                File file2 = new File(schematicsFolder, name + ".schem");
                if(!file.exists() || file.isAbsolute()) {
                    if(!file2.exists() || file2.isAbsolute()){
                        errorMessage(event, "The schematic " + name + " does not exist.");
                        return;
                    }
                }
                if (file.exists() && file.length() > 8388608){ //If file is larger than 8MB
                    errorMessage(event, "The schematic is too large to upload through Discord.");
                } else if (file2.exists() && file2.length() > 8388608){
                    errorMessage(event, "The schem is too large to upload through Discord.");
                } else {
                    if(file.exists()){
                        event.getChannel().sendFile(file).queue();
                    }
                    if(file2.exists()){
                        event.getChannel().sendFile(file2).queue();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
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
