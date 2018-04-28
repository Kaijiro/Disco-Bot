package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

@Command(name="!ping")
public class PingPongCommand extends AbstractBotCommand {

    private static Logger logger = LogManager.getLogger(PingPongCommand.class);

    @Override
    public void execute(MessageReceivedEvent event) {
        if(event.getMessage().getContent().equals("!ping")){
            MessageBuilder builder = new MessageBuilder(event.getClient());

            RequestBuffer.request(() -> {
                builder
                        .withContent("pong !")
                        .withChannel(event.getMessage().getChannel());

                builder.send();
            });
        }
    }
}
