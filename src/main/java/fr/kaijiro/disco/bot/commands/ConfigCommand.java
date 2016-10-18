package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.application.DiscoBot;
import fr.kaijiro.disco.bot.configuration.ArgumentsParser;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.GuildConfigManager;
import fr.kaijiro.disco.bot.entities.GuildConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.nio.file.FileSystemException;

// TODO Find a way to improve the registering of "simple" commands like this one (note : sdcf4j doesn't work)
public class ConfigCommand implements IListener<MessageReceivedEvent> {

    private static Logger logger = LogManager.getLogger(ConfigCommand.class);

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        IMessage message = messageReceivedEvent.getMessage();

        if(message.getContent().startsWith("!config")){
            String[] parts = message.getContent().split(" ");

            if(parts.length == 3 && parts[1].equals("log")){
                String channelName = parts[2];

                IDiscordClient client = DiscoBot.getInstance();
                IChannel channel = null;

                for(IChannel _channel : client.getChannels()){
                    if(_channel.getName().equals(channelName)){
                        channel = _channel;
                    }
                }

                if(channel == null){
                    DiscoBot.sendMessage("Could not find channel " + channelName, message.getChannel());
                    return;
                }

                try{
                    GuildConfigManager guildConfigManager = new GuildConfigManager(ArgumentsParser.get(DiscoBotOption.CONFIGURATION_DIRECTORY));

                    GuildConfig config = GuildConfigManager.get(channel.getGuild().getID());

                    if(config == null){
                        config = new GuildConfig();
                    }

                    config.setLogChannel(channel.getID());
                    guildConfigManager.register(channel.getGuild().getID(), config);

                    DiscoBot.sendMessage("Config registered !", message.getChannel());
                }
                catch(FileSystemException ex){
                    logger.error("Error : " + ex.getMessage());

                    DiscoBot.sendMessage("An error occured, please contact the developper.", message.getChannel());
                }

            }
        }
    }
}
