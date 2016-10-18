package fr.kaijiro.disco.bot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.kaijiro.disco.bot.entities.GuildConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

public class GuildConfigManager {

    private static final Map<String, GuildConfig> configs = new HashMap<>();
    private File configDirectory;

    private static Logger logger = LogManager.getLogger(GuildConfigManager.class);

    public GuildConfigManager(String configPath) throws FileSystemException {
        this.configDirectory = new File(configPath);

        if(this.configDirectory.exists() && !this.configDirectory.isDirectory())
            throw new FileSystemException("File " + configPath + " already exists and is not a directory.");

        if(!this.configDirectory.exists()) {
            if (!this.configDirectory.mkdir()) {
                throw new FileSystemException("Could not create the directory " + configPath);
            }
        }
        else {
            if(!this.configDirectory.canWrite() || !this.configDirectory.canRead())
                throw new FileSystemException("Insufficient permissions in the directory " + configPath);
        }
    }

    public void load(){
        ObjectMapper mapper = new ObjectMapper();
        for(File file : this.configDirectory.listFiles()){
            try{
                GuildConfig guildConfig = mapper.readValue(file, GuildConfig.class);
                GuildConfigManager.configs.put(file.getName(), guildConfig);
            } catch (IOException e) {
                logger.error("Config loading error : " + e.getMessage());
                System.exit(-2);
            }
        }

        logger.debug(GuildConfigManager.configs);
    }

    public boolean register(String channelId, GuildConfig config){
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(this.configDirectory.getAbsolutePath() + File.separator + channelId);

        try {
            mapper.writeValue(file, config);
            configs.put(channelId, config);

            logger.debug(configs.values());

            return true;
        } catch (IOException e) {
            logger.error("Config write error : " + e.getMessage());
            return false;
        }
    }

    public static GuildConfig get(String guildId){
        return configs.get(guildId);
    }
}
