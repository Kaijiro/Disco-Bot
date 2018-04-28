package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class AbstractBotCommand implements IListener<MessageReceivedEvent> {

    @Override
    public void handle(MessageReceivedEvent event) {
        // Check if the message contains a command
        String commandName = this.getClass().getAnnotation(Command.class).name();

        if(event.getMessage().getContent().startsWith(commandName)){
            this.execute(event);
        }
    }

    public abstract void execute(MessageReceivedEvent event);
}
