package fr.kaijiro.disco.bot.commands;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.kaijiro.disco.bot.annotations.Command;

@Command("!config")
public class ConfigCommand extends AbstractBotCommand {

    private static Logger logger = LogManager.getLogger(ConfigCommand.class);

    @Override
    public void execute(Map<String, String> parameters) {
        String message = this.event.getMessage().getContent().orElse("");
        if(message.startsWith("!config")){
            String[] parts = message.split(" ");

            if(parts.length == 3 && parts[1].equals("log")){
                String channelName = parts[2];

//                IDiscordClient client = this.event.getClient();
//                IChannel channel = null;
//
//                for(IChannel _channel : client.getChannels()){
//                    if(_channel.getName().equals(channelName)){
//                        channel = _channel;
//                    }
//                }
//
//                if(channel == null){
//                    builder
//                        .withContent("Could not find channel " + channelName)
//                        .withChannel(this.event.getMessage().getChannel());
//
//                    RequestBuffer.request(builder::send);
//                    return;
//                }
//
//                try{
//                    GuildConfigManager guildConfigManager = new GuildConfigManager(SystemEnv.getOrNull(DiscoBotOption.CONFIGURATION_DIRECTORY));
//
//                    GuildConfig config = GuildConfigManager.get(channel.getGuild().getLongID());
//
//                    if(config == null){
//                        config = new GuildConfig();
//                    }
//
//                    config.setLogChannel(channel.getLongID());
//                    guildConfigManager.register(channel.getGuild().getLongID(), config);
//
//                    builder
//                            .withContent("Config registered !")
//                            .withChannel(this.event.getMessage().getChannel());
//
//                    RequestBuffer.request(builder::send);
//                }
//                catch(FileSystemException ex){
//                    logger.error("Error : " + ex.getMessage());
//
//                    builder
//                            .withContent("An error occured, please contact the developper.")
//                            .withChannel(this.event.getMessage().getChannel());
//
//                    RequestBuffer.request(builder::send);
//                }
            }
        }
    }

    @Override
    public void formatHelp() {

    }
}
