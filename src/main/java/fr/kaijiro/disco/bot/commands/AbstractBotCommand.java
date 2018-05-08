package fr.kaijiro.disco.bot.commands;

import com.google.common.collect.Lists;
import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.DiscoCommandParser;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingParameterException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingValueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractBotCommand implements IListener<MessageReceivedEvent> {

    protected MessageReceivedEvent event;

    protected List<Parameter> parameters = new ArrayList<>();

    private static Logger logger = LogManager.getLogger(AbstractBotCommand.class);

    @Override
    public void handle(MessageReceivedEvent event) {
        this.event = event;
        // Check if the message contains a command
        String commandName = this.getClass().getAnnotation(Command.class).value();
        String[] aliases = this.getClass().getAnnotation(Command.class).aliases();

        List<String> aliasList = Lists.newArrayList(aliases);

        if(event.getMessage().getContent().startsWith(commandName) || aliasList.stream().anyMatch(e -> !e.isEmpty() && event.getMessage().getContent().startsWith(e))){
            try{
                // Check if parameters are well formatted
                Map<String, String> params = this.checkCommandArgs();
                this.execute(params);
            } catch(MissingParameterException | MissingValueException e) {
                RequestBuffer.request(() -> {
                    MessageBuilder builder = new MessageBuilder(this.event.getClient());
                    builder.withContent(e.getMessage());
                    this.formatHelp(builder);
                    builder.send();
                });
            } catch(Exception e){
                logger.error(e.getMessage(), e);
                RequestBuffer.request(() -> {
                    MessageBuilder builder = new MessageBuilder(this.event.getClient());
                    builder.withChannel(this.event.getChannel());
                    builder.withContent("An error happened ! Check the logs or contact the devs ....").send();
                });
            }
        }
    }

    public List<Parameter> getParameters(){
        return this.parameters;
    }

    public String getCommandNameShort(){
        return this.getClass().getAnnotation(Command.class).value();
    }

    private Map<String, String> checkCommandArgs() throws MissingValueException, MissingParameterException {
        String cmdSent = this.event.getMessage().getContent();
        DiscoCommandParser parser = new DiscoCommandParser();

        return parser.parse(cmdSent, this.getParameters());
    }

    public abstract void execute(Map<String, String> parameters);

    public abstract void formatHelp(MessageBuilder builder);
}
