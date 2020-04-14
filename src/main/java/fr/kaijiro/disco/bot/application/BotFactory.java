package fr.kaijiro.disco.bot.application;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import fr.kaijiro.disco.bot.commands.ConfigCommand;
import fr.kaijiro.disco.bot.commands.HangmanCommand;
import fr.kaijiro.disco.bot.commands.PingPongCommand;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.SystemEnv;
import fr.kaijiro.disco.bot.configuration.exceptions.ValueNotSetException;

public class BotFactory {

    private String token;

    private DiscordClient botInstance;

    private static final Logger logger = LogManager.getLogger(BotFactory.class);

    public static DiscordClient buildBot(){
        return new BotFactory().getBotInstance();
    }

    private BotFactory(){
        try {
            this.token = SystemEnv.getOrThrow(DiscoBotOption.BOT_TOKEN);
        } catch (ValueNotSetException e) {
            BotFactory.logger.error(e.getMessage());
            System.exit(-1);
        }

        this.build();
    }

    private void build(){
        DiscordClientBuilder clientBuilder = DiscordClientBuilder.create(this.token);

        this.botInstance = clientBuilder.build();
        this.registerCommandsAndListeners();
    }

//    private void registerCommandsAndListeners(){
//        Reflections reflects = new Reflections("fr.kaijiro.disco.bot");
//        Set<Class<?>> commandClasses = reflects.getTypesAnnotatedWith(Command.class);
//        Set<Class<?>> listenerClasses = reflects.getTypesAnnotatedWith(Listener.class);
//
//        commandClasses.forEach(c -> {
//            try {
//                this.instantiateAndRegister(c);
//            } catch (InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        });
//        listenerClasses.forEach(this::instantiateAndRegister);
////        commandClasses.forEach(this::instantiateAndRegister);
////        listenerClasses.forEach(this::instantiateAndRegister);
//    }

    private void registerCommandsAndListeners(){
        Arrays.asList(new ConfigCommand(), new HangmanCommand(), new PingPongCommand())
                .forEach(c -> this.botInstance.getEventDispatcher().on(MessageCreateEvent.class)
                    .subscribe(c::handle));
    }


//    private void instantiateAndRegister(Class<?> c) throws InstantiationException, IllegalAccessException {
//        AbstractBotCommand commmand = (AbstractBotCommand) c.newInstance();
//        this.botInstance.getEventDispatcher().on(MessageCreateEvent.class)
//                .subscribe(commmand::handle);
//    }

    public DiscordClient getBotInstance(){
        return this.botInstance;
    }
}
