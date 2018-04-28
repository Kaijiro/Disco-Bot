package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.application.Main;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.GuildConfigManager;
import fr.kaijiro.disco.bot.entities.GuildConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.nio.file.FileSystemException;

@Command(name = "!config")
public class ConfigCommand extends AbstractBotCommand {

    private static Logger logger = LogManager.getLogger(ConfigCommand.class);

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageBuilder builder = new MessageBuilder(event.getClient());
        IMessage message = event.getMessage();

        if(message.getContent().startsWith("!config")){
            String[] parts = message.getContent().split(" ");

            if(parts.length == 3 && parts[1].equals("log")){
                String channelName = parts[2];

                IDiscordClient client = event.getClient();
                IChannel channel = null;

                for(IChannel _channel : client.getChannels()){
                    if(_channel.getName().equals(channelName)){
                        channel = _channel;
                    }
                }

                if(channel == null){
                    builder
                        .withContent("Could not find channel " + channelName)
                        .withChannel(event.getMessage().getChannel());

                    RequestBuffer.request(builder::send);
                    return;
                }

                try{
                    GuildConfigManager guildConfigManager = new GuildConfigManager(Main.ApplicationConfiguration.get().get(DiscoBotOption.CONFIGURATION_DIRECTORY));

                    GuildConfig config = GuildConfigManager.get(channel.getGuild().getLongID());

                    if(config == null){
                        config = new GuildConfig();
                    }

                    config.setLogChannel(channel.getLongID());
                    guildConfigManager.register(channel.getGuild().getLongID(), config);

                    builder
                            .withContent("Config registered !")
                            .withChannel(event.getMessage().getChannel());

                    RequestBuffer.request(builder::send);
                }
                catch(FileSystemException ex){
                    logger.error("Error : " + ex.getMessage());

                    builder
                            .withContent("An error occured, please contact the developper.")
                            .withChannel(event.getMessage().getChannel());

                    RequestBuffer.request(builder::send);
                }
            }
        }
    }
}
