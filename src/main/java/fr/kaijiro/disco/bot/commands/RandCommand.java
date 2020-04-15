package fr.kaijiro.disco.bot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.math.NumberUtils;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;

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

        if (NumberUtils.isNumber(request) && Integer.parseInt(request) >= 1) {
            // Nombre boundary
            this.respond(1 + new Random().nextInt(Integer.parseInt(request)) + "");
        } else if (Arrays.stream(request.split("-")).allMatch(NumberUtils::isNumber)) {
            // from - to
            int from = Integer.parseInt(request.split("-")[0]);
            int to = Integer.parseInt(request.split("-")[1]);
            if (from > to) {
                this.formatHelp();
                return;
            }
            this.respond(from + new Random().nextInt(to-from+1) + "");
        } else if (Arrays.stream(request.split("d")).allMatch(NumberUtils::isNumber)) {
            // x dice N
            int x = Integer.parseInt(request.split("d")[0]);
            int n = Integer.parseInt(request.split("d")[1]);
            if (x <= 0 || n <= 0) {
                this.formatHelp();
                return;
            }
            StringBuilder out = new StringBuilder();
            List<Integer> rolls = IntStream.range(0, x).map(e -> 1 + new Random().nextInt(n)).boxed().collect(Collectors.toList());
            for (int i = 0; i < rolls.size(); i++) {
                out.append("Dice").append(i+1).append(": ").append(rolls.get(i)).append("\n");
            }
            out.append("Total: ").append(rolls.stream().mapToInt(Integer::intValue).sum());
            this.respond(out.toString());
        } else {
            this.formatHelp();
        }
    }

    @Override
    public void formatHelp() {
        this.respond(
                "**[" + this.getCommandNameShort() + "]** Commande permettant de générer un nombre aléatoire." +
                "\n*Utilisation :* `" + this.getCommandNameShort() + " bound` *(bound>=1)* | `" + this.getCommandNameShort() + " from-to` *(from>=1 & to>=1)* | `" + this.getCommandNameShort() + " xdN` (x dice N)" +
                (this.getCommandAliases().isEmpty() ? "" : "\n*Aliases :* `" + String.join("`, `", this.getCommandAliases())+ "`")
        );
    }
}
