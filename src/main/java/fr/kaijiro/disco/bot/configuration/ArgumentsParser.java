package fr.kaijiro.disco.bot.configuration;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;

public class ArgumentsParser {

    private final Options options;
    private static Map<DiscoBotOption, String> parametersMap;

    private static Logger logger = LogManager.getLogger(ArgumentsParser.class);

    public ArgumentsParser(){
        options = new Options();

        parametersMap = new EnumMap<>(DiscoBotOption.class);

        for(DiscoBotOption discoBotOption : DiscoBotOption.values()){
            options.addOption(
                Option.builder(discoBotOption.getTinyOpt())
                    .longOpt(discoBotOption.getLongOpt())
                    .hasArg(true)
                    .build()
            );
        }
    }

    public void load(String[] args){
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        try{
            commandLine = parser.parse(this.options, args);

            for(DiscoBotOption discoBotOption : DiscoBotOption.values()){
                if(commandLine.hasOption(discoBotOption.getTinyOpt()) && discoBotOption == DiscoBotOption.HELP){
                    HelpFormatter helpFormatter = new HelpFormatter();
                    helpFormatter.printHelp("disco-bot", options);

                    System.exit(1);
                }

                if(commandLine.hasOption(discoBotOption.getTinyOpt())){
                    parametersMap.put(discoBotOption, commandLine.getOptionValue(discoBotOption.getTinyOpt()));
                }
            }
        }
        catch(ParseException ex){
            logger.error("Could not parse arguments : " + ex.getMessage());

            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("disco-bot", options);

            System.exit(1);
        }
    }

    public static String get(DiscoBotOption key){
        return parametersMap.get(key);
    }
}
