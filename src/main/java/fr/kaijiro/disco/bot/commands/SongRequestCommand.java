package fr.kaijiro.disco.bot.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;

@Command(value = "!songrequest", aliases = {"!sr"})
public class SongRequestCommand extends AbstractBotCommand {

    @Override
    public List<Parameter> getParameters() {
        this.parameters.add(
                Parameter.build("link")
                        .isOptional(false)
                        .hasArg(false)
                        .waitedType(String.class)
        );

        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        if (StringUtils.isBlank(parameters.get("link"))) {
            this.formatHelp();
            return;
        }


    }

    @Override
    public void formatHelp() {
        this.respond(
                "**[" + this.getCommandNameShort() + "]** Commande pour faire une requête de son" +
                "\n*Utilisation :* `" + this.getCommandNameShort() + " <youtube link>`" +
                (this.getCommandAliases().isEmpty() ? "" : "\n*Aliases :* `" + String.join("`, `", this.getCommandAliases())+ "`")
        );
    }
}
