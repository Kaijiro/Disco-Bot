package fr.kaijiro.disco.bot.listeners;

import fr.kaijiro.disco.bot.application.DiscoBot;
import fr.kaijiro.disco.bot.configuration.GuildConfigManager;
import fr.kaijiro.disco.bot.entities.GuildConfig;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserStatusListener implements IListener<PresenceUpdateEvent>{

    public void handle(PresenceUpdateEvent presenceUpdateEvent) {
        IDiscordClient client = presenceUpdateEvent.getClient();
        presenceUpdateEvent.getUser();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        for(IGuild guild : client.getGuilds()){
            if(guild.getUsers().contains(presenceUpdateEvent.getUser())){
                GuildConfig guildConfig = GuildConfigManager.get(guild.getID());
                IChannel channel = client.getChannelByID(guildConfig.getLogChannel());

                DiscoBot.sendMessage(
                        new StringBuilder()
                                .append(presenceUpdateEvent.getUser().getName())
                                .append(" went from ")
                                .append(presenceUpdateEvent.getOldPresence())
                                .append(" to ")
                                .append(presenceUpdateEvent.getNewPresence())
                                .append(" on ")
                                .append(formatter.format(new Date()))
                                .toString()
                        , channel);
                return;
            }
        }

    }
}
