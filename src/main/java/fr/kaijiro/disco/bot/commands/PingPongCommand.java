package fr.kaijiro.disco.bot.commands;

import java.util.List;
import java.util.Map;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;

@Command(value = "!ping", aliases = {"!gnip"})
public class PingPongCommand extends AbstractBotCommand {

    @Override
    public List<Parameter> getParameters() {
        this.parameters.add(
                Parameter.build("times")
                        .isOptional(false)
                        .hasArg(true)
                        .waitedType(Integer.class)
                        .validatedWith(e -> Integer.parseInt(e) > 0 && Integer.parseInt(e) < 10)
        );

        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        for(int i = 0 ; i < Integer.parseInt(parameters.get("times")) ; i++) {
            this.respond("Pong !");
        }

    }

    @Override
    public void formatHelp() {
        this.respond(
                "**[" + this.getCommandNameShort() + "]** Commande pour pinger le bot." +
                "\n*Utilisation :* `" + this.getCommandNameShort() + " <times>`, `<times>` étant le nombre de fois que le bot doit répondre" +
                (this.getCommandAliases().isEmpty() ? "" : "\n*Aliases :* `" + String.join("`, `", this.getCommandAliases())+ "`")
        );
    }
}
