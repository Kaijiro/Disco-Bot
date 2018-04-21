package fr.kaijiro.disco.bot.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscoBot {

    private static Logger logger = LogManager.getLogger(DiscoBot.class);

    private static IDiscordClient instance = null;

    private static String token = "";

    public static IDiscordClient getInstance(String token) {
        if(instance == null) {
            ClientBuilder clientBuilder = new ClientBuilder();
            clientBuilder.withToken(token);
            logger.info("Bot instance built");
            return clientBuilder.build();
        }

        return DiscoBot.instance;
    }

    public static IDiscordClient getInstance() {
        return DiscoBot.getInstance(DiscoBot.getToken());
    }

    public static void setToken(String token){
        DiscoBot.token = token;
    }

    public static String getToken(){
        return DiscoBot.token;
    }

    public static void sendMessage(String message, IChannel channel){
        IDiscordClient client = DiscoBot.getInstance();

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
