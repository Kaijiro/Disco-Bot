package fr.kaijiro.disco.bot.application;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import fr.kaijiro.disco.bot.commands.*;
import fr.kaijiro.disco.bot.configuration.DiscoBotOption;
import fr.kaijiro.disco.bot.configuration.SystemEnv;
import fr.kaijiro.disco.bot.configuration.exceptions.ValueNotSetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class BotFactory {
    private String token;

    private DiscordClient botInstance;

    private static final Logger logger = LogManager.getLogger(BotFactory.class);

    private final List<AbstractBotCommand> botCommands = Arrays.asList(
            new ConfigCommand(),
            new HangmanCommand(),
            new PingPongCommand(),
            new HelpCommand()
    );

    public static DiscordClient buildBot() {
        return new BotFactory().getBotInstance();
    }

    private BotFactory() {
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

    private void registerCommandsAndListeners() {
        botCommands.forEach(command -> this.botInstance.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(command::handle));
    }

    public DiscordClient getBotInstance(){
        return this.botInstance;
    }
}
