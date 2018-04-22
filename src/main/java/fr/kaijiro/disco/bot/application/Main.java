package fr.kaijiro.disco.bot.application;

import fr.kaijiro.disco.bot.configuration.ArgumentsParser;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.GuildConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;

import java.nio.file.FileSystemException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    public static AtomicReference<IDiscordClient> BotInstance = new AtomicReference<>();

    public static AtomicReference<Map<DiscoBotOption, String >> ApplicationConfiguration = new AtomicReference<>();

    // Don't forget to click there : https://discordapp.com/oauth2/authorize?client_id=233332037333811201&scope=bot&permissions=-1
    public static void main(String[] args) {
        // Parse program arguments and format them into a map
        ArgumentsParser argumentsParser = new ArgumentsParser();
        argumentsParser.load(args);
        Main.ApplicationConfiguration.set(argumentsParser.getParametersMap());

        // Check if folder exists, if not create. Check if writable.
        try {
            GuildConfigManager guildConfigManager = new GuildConfigManager(argumentsParser.get(DiscoBotOption.CONFIGURATION_DIRECTORY));
            guildConfigManager.load();
        }
        catch(FileSystemException ex){
            logger.error("Error with the configurations directory : " + ex.getMessage());
            System.exit(-1);
        }

        // Build bot instance
        BotFactory factory = BotFactory.buildFactory(argumentsParser.getParametersMap());
        IDiscordClient bot = factory.getBotInstance();
        Main.BotInstance.set(bot);

        // We're ready to go !
        logger.info("DiscoBot ready to go !");
        bot.login();
    }
}
