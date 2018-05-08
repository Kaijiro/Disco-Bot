package fr.kaijiro.disco.bot.commands.parameters;

import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingParameterException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingValueException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoCommandParser {

    private Map<String, String> parameterMap;

    public Map<String, String> parse(String cmd, List<Parameter> params) throws MissingValueException, MissingParameterException {
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

                this.parameterMap.put(_param.getName(), cmdElements[i]);
            }

            for(Parameter param : params){
                if(elm.equals(param.getName()) || elm.startsWith(param.getName() + "=")){
                    // Check if param is waiting for a value
                    if(param.hasArg()){
                        // Read the next parameter
                        if(i + 1 == cmdElements.length)
                            throw new MissingValueException();
                        else
                            this.parameterMap.put(param.getName(), cmdElements[++i]);
                    } else {
                        this.parameterMap.put(param.getName(), "");
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

    public DiscoCommandParser(){
        this.parameterMap = new HashMap<>();
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }
}
