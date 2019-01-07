package fr.kaijiro.disco.bot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

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

        Reflections reflections = new Reflections("fr.kaijiro.disco.bot.commands");
        Set<Class<? extends AbstractBotCommand>> classes = reflections.getSubTypesOf(AbstractBotCommand.class);

        List<String> commandNames = classes.stream()
                .map(c -> Arrays.asList(c.getAnnotationsByType(Command.class)))
                .flatMap(List::stream)
                .map(Command::value)
                .collect(Collectors.toList());

        RequestBuffer.request(() -> {
            builder
                    .withContent("Les commandes disponibles sont les suivantes : \n\n" + StringUtils.join(commandNames, "\n"))
                    .withChannel(this.event.getChannel());

            builder.send();
        });
    }

    @Override
    public void formatHelp(MessageBuilder builder) {
        builder.appendContent("Commande donnant la liste des commandes disponnibles");
    }
}
