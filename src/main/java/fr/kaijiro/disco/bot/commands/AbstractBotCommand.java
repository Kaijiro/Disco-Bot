package fr.kaijiro.disco.bot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import discord4j.core.event.domain.message.MessageCreateEvent;
import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.DiscoCommandParser;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.IncorrectValueException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingParameterException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingValueException;

public abstract class AbstractBotCommand {

    protected MessageCreateEvent event;
    protected List<Parameter> parameters = new ArrayList<>();
    protected final Logger logger = LogManager.getLogger(this.getClass());

    public void handle(MessageCreateEvent event) {
        this.event = event;

        String message = event.getMessage().getContent().orElse("");

        if (this.commandShouldBeInvoked(message)) {
            try {
                // Check if parameters are well formatted
                Map<String, String> params = this.checkCommandArgs();
                this.execute(params);
            } catch (MissingParameterException | MissingValueException | IncorrectValueException e) {
                this.logger.error(e.getMessage());
                this.formatHelp();
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
                this.respond("An error happened ! Check the logs or contact the devs ....");
            }
        }
    }

    private boolean commandShouldBeInvoked(String message) {
        String commandName = this.getClass().getAnnotation(Command.class).value();
        ArrayList<String> aliases = Lists.newArrayList(this.getClass().getAnnotation(Command.class).aliases());
        boolean messageUseAnyAlias = aliases.stream().anyMatch(e -> !e.isEmpty() && message.startsWith(e));

        return message.startsWith(commandName) || messageUseAnyAlias;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public String getCommandNameShort() {
        return this.getClass().getAnnotation(Command.class).value();
    }

    public List<String> getCommandAliasesShort() {
        return Arrays.asList(this.getClass().getAnnotation(Command.class).aliases());
    }

    private Map<String, String> checkCommandArgs() throws MissingValueException, MissingParameterException, IncorrectValueException {
        String cmdSent = this.event.getMessage().getContent().orElse("");
        DiscoCommandParser parser = new DiscoCommandParser();

        return parser.parse(cmdSent, this.getParameters());
    }

    public void respond(String message) {
        this.event.getMessage()
                .getChannel()
                .subscribe(messageChannel -> messageChannel.createMessage(message).subscribe());
    }

    public abstract void execute(Map<String, String> parameters);

    public abstract void formatHelp();
}
