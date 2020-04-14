package fr.kaijiro.disco.bot.application;

import java.nio.file.FileSystemException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import discord4j.core.DiscordClient;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.GuildConfigManager;
import fr.kaijiro.disco.bot.configuration.SystemEnv;
import fr.kaijiro.disco.bot.configuration.exceptions.ValueNotSetException;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    // Don't forget to click there : https://discordapp.com/oauth2/authorize?client_id=699635381192097824&scope=bot&permissions=-1
    public static void main(String[] args) {
        // Check if folder exists, if not create. Check if writable.
        try {
            GuildConfigManager guildConfigManager = new GuildConfigManager(SystemEnv.getOrThrow(DiscoBotOption.CONFIGURATION_DIRECTORY));
            guildConfigManager.load();
        }
        catch(FileSystemException ex){
            logger.error("Error with the configurations directory : " + ex.getMessage());
            System.exit(-1);
        }
        catch(ValueNotSetException ex){
            Main.logger.error(ex.getMessage());
            System.exit(-1);
        }

        // Build bot instance
        DiscordClient bot = BotFactory.buildBot();

        // We're ready to go !
        logger.info("DiscoBot ready to go !");
        bot.login().block();
    }
}
