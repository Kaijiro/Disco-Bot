package fr.kaijiro.disco.bot.commands;

import com.google.common.collect.Lists;
import fr.kaijiro.disco.bot.annotations.Command;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public abstract class AbstractBotCommand implements IListener<MessageReceivedEvent> {

    @Override
    public void handle(MessageReceivedEvent event) {
        // Check if the message contains a command
        String commandName = this.getClass().getAnnotation(Command.class).value();
        String[] aliases = this.getClass().getAnnotation(Command.class).aliases();

        List<String> aliasList = Lists.newArrayList(aliases);

        if(event.getMessage().getContent().startsWith(commandName) || aliasList.stream().anyMatch(e -> event.getMessage().getContent().startsWith(e))){
            this.execute(event);
        }
    }

    public abstract void execute(MessageReceivedEvent event);
}
