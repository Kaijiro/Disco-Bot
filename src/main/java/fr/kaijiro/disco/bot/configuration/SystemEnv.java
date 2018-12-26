package fr.kaijiro.disco.bot.configuration;

import fr.kaijiro.disco.bot.configuration.exceptions.ValueNotSetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemEnv {

    private static Logger logger = LogManager.getLogger(SystemEnv.class);

    public static String getOrNull(String key){
        return System.getenv(key.toUpperCase());
    }

    public static String getOrNull(DiscoBotOption key){
        return SystemEnv.getOrNull(key.name().toUpperCase());
    }

    public static String getOrThrow(String key) throws ValueNotSetException{
        String envValue = System.getenv(key.toUpperCase());

        if(envValue == null)
            throw new ValueNotSetException(key + " env var is not declared");

        return envValue;
    }

    public static String getOrThrow(DiscoBotOption key) throws ValueNotSetException{
        return SystemEnv.getOrThrow(key.name().toUpperCase());
    }
}
