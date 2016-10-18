package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.application.DiscoBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

public class PingPongCommand implements IListener<MessageReceivedEvent> {

    private static Logger logger = LogManager.getLogger(PingPongCommand.class);

    public void handle(MessageReceivedEvent event) {
        if(event.getMessage().getContent().equals("!ping")){
            MessageBuilder builder = new MessageBuilder(DiscoBot.getInstance());

            builder
                .withContent("pong !")
                .withChannel(event.getMessage().getChannel());
        }
    }
}
