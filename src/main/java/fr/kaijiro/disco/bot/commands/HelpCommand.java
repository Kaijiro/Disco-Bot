package fr.kaijiro.disco.bot.commands;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

@Command("!help")
public class HelpCommand extends AbstractBotCommand {

    private static Logger logger = LogManager.getLogger(HelpCommand.class);

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        MessageBuilder builder = new MessageBuilder(this.event.getClient());

        RequestBuffer.request(() -> {
            builder
                    .withContent("test !")
                    .withChannel(this.event.getChannel());

            builder.send();
        });
    }

    @Override
    public void formatHelp(MessageBuilder builder) {
        builder.appendContent("Commande donnant la liste des commandes disponnibles");
    }
}
