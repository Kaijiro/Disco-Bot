package fr.kaijiro.disco.bot.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscoBot {

    private static Logger logger = LogManager.getLogger(DiscoBot.class);

    public static void sendMessage(String message, IChannel channel){
        IDiscordClient client = Main.BotInstance.get();

        MessageBuilder builder = new MessageBuilder(client);

        builder
            .withContent(message)
            .withChannel(channel);

        try {
            builder.send();
        } catch (RateLimitException e) {
            logger.error("Could not send message : rate limit reached.");
        } catch (DiscordException e) {
            logger.error("An error occurred : " + e.getMessage());
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            logger.error("DiscoBot does not have permissions to speak in this channel.");
        }
    }
}
