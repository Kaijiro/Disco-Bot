package fr.kaijiro.disco.bot.application;

import fr.kaijiro.disco.bot.commands.ConfigCommand;
import fr.kaijiro.disco.bot.commands.PingPongCommand;
import fr.kaijiro.disco.bot.configuration.ArgumentsParser;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.GuildConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;

import java.nio.file.FileSystemException;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Don't forget to click there : https://discordapp.com/oauth2/authorize?client_id=233332037333811201&scope=bot&permissions=-1
        ArgumentsParser argumentsParser = new ArgumentsParser();
        argumentsParser.load(args);

        // Check if folder exists, if not create. Check if writable.
        try {
            GuildConfigManager guildConfigManager = new GuildConfigManager(argumentsParser.get(DiscoBotOption.CONFIGURATION_DIRECTORY));
            guildConfigManager.load();
        }
        catch(FileSystemException ex){
            logger.error("Error with the configurations directory : " + ex.getMessage());
            System.exit(-1);
        }

        DiscoBot.setToken(argumentsParser.get(DiscoBotOption.BOT_TOKEN));

        IDiscordClient client = DiscoBot.getInstance();

        // TODO Create a method to register every command & listener : scan annotations in packages ?
        client.getDispatcher().registerListener(new PingPongCommand());
        logger.info("PingPongCommand registered");
        client.getDispatcher().registerListener(new ConfigCommand());
        logger.info("ConfigCommand registered");

        client.login();
    }
}
