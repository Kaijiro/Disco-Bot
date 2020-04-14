package fr.kaijiro.disco.bot.commands;

import java.util.ArrayList;
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

    private static Logger logger = LogManager.getLogger(AbstractBotCommand.class);

    public void handle(MessageCreateEvent event) {
        this.event = event;
        // Check if the message contains a command
        String commandName = this.getClass().getAnnotation(Command.class).value();
        String[] aliases = this.getClass().getAnnotation(Command.class).aliases();

        List<String> aliasList = Lists.newArrayList(aliases);

        String msg = event.getMessage().getContent().orElse("");

        if(msg.startsWith(commandName) || aliasList.stream().anyMatch(e -> !e.isEmpty() && msg.startsWith(e))){
            try{
                // Check if parameters are well formatted
                Map<String, String> params = this.checkCommandArgs();
                this.execute(params);
            } catch(MissingParameterException | MissingValueException | IncorrectValueException e) {
                logger.error(e.getMessage(), e);
                this.respond(e.getMessage() + "\n");
            } catch(Exception e){
                logger.error(e.getMessage(), e);
                this.respond("An error happened ! Check the logs or contact the devs ....");
            }
        }
    }

    public List<Parameter> getParameters(){
        return this.parameters;
    }

    public String getCommandNameShort(){
        return this.getClass().getAnnotation(Command.class).value();
    }

    private Map<String, String> checkCommandArgs() throws MissingValueException, MissingParameterException, IncorrectValueException {
        String cmdSent = this.event.getMessage().getContent().orElse("");
        DiscoCommandParser parser = new DiscoCommandParser();

        return parser.parse(cmdSent, this.getParameters());
    }

    public abstract void execute(Map<String, String> parameters);

    public abstract void formatHelp();

    public void respond(String msg) {
        this.event.getMessage().getChannel().block().createMessage(msg).block();
    }
}
