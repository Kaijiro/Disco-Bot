package fr.kaijiro.disco.bot.commands.parameters;

import fr.kaijiro.disco.bot.commands.parameters.exceptions.IncorrectValueException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingParameterException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingValueException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoCommandParser {

    private Map<String, String> parameterMap;

    public Map<String, String> parse(String cmd, List<Parameter> params) throws MissingValueException, MissingParameterException, IncorrectValueException {
        String[] cmdElements = cmd.split(" ");
        // Drop the first part the command, it's only the name
        cmdElements = ArrayUtils.subarray(cmdElements, 1, cmdElements.length);

        long mandatoryParameterCount = params.stream().filter(e -> !e.isOptional()).count();

        // Extract optional parameter
        for(int i = 0; i < cmdElements.length ; i++){
            // Find which param does this element correspond to
            String elm = cmdElements[i];

            if(i <= mandatoryParameterCount - 1){
                /*
                  Mandatory parameters must be specified first : throw an exception if a optional param is found here
                  The "break" instruction will lead the program to the next "for" where we check if all mandatory parameters
                  Are present
                 */
                if(params.stream().anyMatch(e -> elm.startsWith(e.getName() + "=") || elm.equals(e.getName())))
                    break;

                // Is it a mandatory param ?
                Parameter _param = params.get(i);

                this.affectParam(_param, cmdElements[i]);
            }

            for(Parameter param : params){
                if(elm.equals(param.getName()) || elm.startsWith(param.getName() + "=")){
                    // Check if param is waiting for a value
                    if(param.hasArg()){
                        // Read the next parameter
                        if(i + 1 == cmdElements.length)
                            throw new MissingValueException();
                        else
                            this.affectParam(param, cmdElements[++i]);
                    } else {
                        this.affectParam(param, "");
                    }
                }
            }
        }

        // Check command validity : every mandatory param must have been filled
        for(Parameter param : params){
            if(!param.isOptional() && !this.parameterMap.containsKey(param.getName()))
                throw new MissingParameterException("Le paramètre \"" + param.getName() + "\" est manquant !");
        }

        return this.parameterMap;
    }

    private void affectParam(Parameter param, String value) throws IncorrectValueException {
        if(param.getValidator() != null) {
            // Execute type check before validation rule
            switch(param.getWaitedType().getSimpleName()){
                case "String":
                    break;
                case "Integer":
                    try{ Integer.valueOf(value); }
                    catch (NumberFormatException e){
                        throw new IncorrectValueException("La valeur \"" + value + "\" pour le paramètre \"" + param.getName() + "\" n'est pas valide");
                    }
                    break;
                case "Float":
                    try{ Float.valueOf(value); }
                    catch (NumberFormatException e){
                        throw new IncorrectValueException("La valeur \"" + value + "\" pour le paramètre \"" + param.getName() + "\" n'est pas valide");
                    }
                    break;
                case "Double":
                    try{ Double.valueOf(value); }
                    catch (NumberFormatException e){
                        throw new IncorrectValueException("La valeur \"" + value + "\" pour le paramètre \"" + param.getName() + "\" n'est pas valide");
                    }
                    break;
                case "Boolean":
                    if(value.equals("true") || value.equals("false"))
                        break;
                    else
                        throw new IncorrectValueException("La valeur \"" + value + "\" pour le paramètre \"" + param.getName() + "\" n'est pas valide");

                default: break;
            }

            // Execute parameter validation to check if the value is correct
            if (!param.getValidator().apply(value))
                throw new IncorrectValueException("La valeur \"" + value + "\" pour le paramètre \"" + param.getName() + "\" n'est pas valide");
        }

        this.parameterMap.put(param.getName(), value);
    }

    public DiscoCommandParser(){
        this.parameterMap = new HashMap<>();
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }
}
