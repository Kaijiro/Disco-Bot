package fr.kaijiro.disco.bot.commands;

import java.util.Map;

import fr.kaijiro.disco.bot.annotations.Command;

@Command("!config")
public class ConfigCommand extends AbstractBotCommand {

    @Override
    public void execute(Map<String, String> parameters) {
//        String message = this.event.getMessage().getContent().orElse("");
//        if(message.startsWith("!config")){
//            String[] parts = message.split(" ");
//
//            if(parts.length == 3 && parts[1].equals("log")){
//                String channelName = parts[2];
//            }
//        }
        this.formatHelp();
    }

    @Override
    public void formatHelp() {
        this.respond(
                "**[" + this.getCommandNameShort() + "]** This command is not ready yet"+
                (this.getCommandAliases().isEmpty() ? "" : "\n*Aliases :* `" + String.join("`, `", this.getCommandAliases())+ "`")
        );
    }
}
