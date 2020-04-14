package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;

import java.util.Map;

@Command("!config")
public class ConfigCommand extends AbstractBotCommand {

    @Override
    public void execute(Map<String, String> parameters) {
        String message = this.event.getMessage().getContent().orElse("");
        if(message.startsWith("!config")){
            String[] parts = message.split(" ");

            if(parts.length == 3 && parts[1].equals("log")){
                String channelName = parts[2];
            }
        }
    }

    @Override
    public void formatHelp() {
    }
}
