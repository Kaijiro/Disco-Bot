package fr.kaijiro.disco.bot.commands;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;

@Command(value = "!ping", aliases = {"!gnip"})
public class PingPongCommand extends AbstractBotCommand {

    private static Logger logger = LogManager.getLogger(PingPongCommand.class);

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
        this.respond("Pour utiliser cette commande voici le format à suivre : `!ping <times>`, `<times>` " +
                "étant le nombre de fois que le bot doit répondre");
    }
}
