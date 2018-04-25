package fr.kaijiro.disco.bot.application;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.annotations.Listener;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.util.Map;
import java.util.Set;

public class BotFactory {

    private String token;

    private IDiscordClient botInstance;

    private static Logger logger = LogManager.getLogger(BotFactory.class);

    public static IDiscordClient buildBot(Map<DiscoBotOption, String> params){
        return new BotFactory(params).getBotInstance();
    }

    private BotFactory(Map<DiscoBotOption, String> params){
        this.token = params.get(DiscoBotOption.BOT_TOKEN);

        this.build();
    }

    private void build(){
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);

        this.botInstance = clientBuilder.build();
        this.registerCommandsAndListeners();
    }

    private void registerCommandsAndListeners(){
        Reflections reflects = new Reflections("fr.kaijiro.disco.bot");
        Set<Class<?>> commandClasses = reflects.getTypesAnnotatedWith(Command.class);
        Set<Class<?>> listenerClasses = reflects.getTypesAnnotatedWith(Listener.class);

        commandClasses.forEach(this::instantiateAndRegister);
        listenerClasses.forEach(this::instantiateAndRegister);
    }

    private void instantiateAndRegister(Class c){
        try {
            this.botInstance.getDispatcher().registerListener(c.newInstance());
            logger.info(c.getSimpleName() + " registered");
        } catch (InstantiationException | IllegalAccessException ex) {
            logger.error("Could not register " + c.getSimpleName() + " : " + ex.getMessage());
        }
    }

    public IDiscordClient getBotInstance(){
        return this.botInstance;
    }
}
