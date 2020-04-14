package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Command("!help")
public class HelpCommand extends AbstractBotCommand {

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        Reflections reflections = new Reflections("fr.kaijiro.disco.bot.commands");
        Set<Class<? extends AbstractBotCommand>> classes = reflections.getSubTypesOf(AbstractBotCommand.class);

        List<String> commandNames = classes.stream()
                .map(c -> Arrays.asList(c.getAnnotationsByType(Command.class)))
                .flatMap(List::stream)
                .map(Command::value)
                .collect(Collectors.toList());

        this.respond("Les commandes disponibles sont les suivantes : \n\n" + StringUtils.join(commandNames, "\n"));
    }

    @Override
    public void formatHelp() {
        this.respond("Commande donnant la liste des commandes disponnibles");
    }
}
