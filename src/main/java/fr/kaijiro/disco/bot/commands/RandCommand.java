package fr.kaijiro.disco.bot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import org.apache.commons.lang3.math.NumberUtils;

@Command(value = "!rand", aliases = {"!roll"})
public class RandCommand extends AbstractBotCommand {

    @Override
    public List<Parameter> getParameters() {
        if (!this.parameters.contains(Parameter.build("request"))) {
            this.parameters.add(
                    Parameter.build("request")
                            .isOptional(false)
                            .hasArg(false)
                            .waitedType(String.class)
                            .validatedWith(e -> NumberUtils.isNumber(e) || Arrays.stream(e.split("-")).allMatch(NumberUtils::isNumber) || Arrays.stream(e.split("d")).allMatch(NumberUtils::isNumber))
            );
        }
        return this.parameters;
    }

    @Override
    public void execute(Map<String, String> parameters) {
        String request = parameters.get("request");

        if(NumberUtils.isNumber(request)) {
            // Nombre boundary
            this.respond(1 + new Random().nextInt(Integer.parseInt(request)) + "");
        } else if (Arrays.stream(request.split("-")).allMatch(NumberUtils::isNumber)) {
            // from - to
            int from = Integer.parseInt(request.split("-")[0]);
            int to = Integer.parseInt(request.split("-")[1]);
            if (from > to) {
                formatHelp();
                return;
            }
            this.respond(from + new Random().nextInt(to-from+1) + "");
        } else if (Arrays.stream(request.split("d")).allMatch(NumberUtils::isNumber)) {
            // x dice N
            int x = Integer.parseInt(request.split("d")[0]);
            int n = Integer.parseInt(request.split("d")[1]);
            if (x <= 0 || n <= 0) {
                formatHelp();
                return;
            }
            StringBuilder out = new StringBuilder();
            List<Integer> rolls = IntStream.range(0, x).map(e -> 1 + new Random().nextInt(n)).boxed().collect(Collectors.toList());
            for (int i = 0; i < rolls.size(); i++) {
                out.append("Roll" + (i+1) + ":"+rolls.get(i)+"\n");
            }
//            out.append("Total:"+rolls.stream().mapToInt(Integer::parseInt()).sum())
//            this.respond(out);
        } else {
            formatHelp();
        }
    }

    @Override
    public void formatHelp() {
        // Todo: donner  les limites
        this.respond("Commande permettant de générer un nombre aléatoire, utilisation : `!rand boundary` or `!rand from-to` or `!rand xdN` (x dice N)");
    }
}
