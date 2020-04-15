package fr.kaijiro.disco.bot.commands;

import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;

@Command("!rand")
public class RandCommand extends AbstractBotCommand {

    @Override
    public List<Parameter> getParameters() {
//        if(!this.parameters.contains(Parameter.build("from"))) {
//            this.parameters.add(
//                    Parameter.build("from")
//                            .isOptional(false)
//                            .hasArg(false)
//                            .waitedType(Integer.class)
//                            .validatedWith(e -> Integer.parseInt(e) >= 0)
//            );
//        }
        if(!this.parameters.contains(Parameter.build("to"))) {
            this.parameters.add(
                    Parameter.build("to")
                            .isOptional(false)
                            .hasArg(false)
                            .waitedType(Integer.class)
                            .validatedWith(e -> Integer.parseInt(e) > 1)
            );
        }
        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        if (parameters.get("from") == null) {
            this.logger.debug("From parameter was empty");
            parameters.put("from", "1");
        }
        if (parameters.get("to") == null) {
            this.logger.debug("To parameter was empty");
            parameters.put("to", "100");
        }
        int from = Integer.parseInt(parameters.get("from"));
        int to = Integer.parseInt(parameters.get("to"));

        Random rand = new Random();

        int out = from + rand.nextInt(to);
        this.respond(String.valueOf(out));
    }

    @Override
    public void formatHelp() {
        // Todo: donner  les limites
        this.respond("Commande permettant de générer un nombre aléatoire");
    }
}
