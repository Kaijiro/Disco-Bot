package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;
import java.util.Map;

@Command(value = "!ping", aliases = {"!gnip"})
public class PingPongCommand extends AbstractBotCommand {

    private static Logger logger = LogManager.getLogger(PingPongCommand.class);

    @Override
    public List<Parameter> getParameters() {
        this.parameters.add(Parameter.build("times").isOptional(false).hasArg(true));

        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        MessageBuilder builder = new MessageBuilder(this.event.getClient());

        for(int i = 0 ; i < Integer.parseInt(parameters.get("times")) ; i++) {
            RequestBuffer.request(() -> {
                builder
                        .withContent("pong !")
                        .withChannel(this.event.getChannel());

                builder.send();
            });
        }
    }

    @Override
    public void formatHelp(MessageBuilder builder) {
        builder.appendContent("Pour utiliser cette commande voici le format à suivre : `!ping <times>`, `<times>`" +
                "étant le nombre de fois que le bot doit répondre");
    }
}
