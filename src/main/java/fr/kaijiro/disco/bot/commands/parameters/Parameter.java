package fr.kaijiro.disco.bot.commands.parameters;

public class Parameter {
    private String name;

    private boolean hasArg = false;

    private boolean isOptional = true;

    private String defaultValue = "";

    public String getName() {
        return name;
    }

    public Parameter setName(String name) {
        this.name = name;

        return this;
    }

    public boolean hasArg() {
        return hasArg;
    }

    public Parameter hasArg(boolean hasArg) {
        this.hasArg = hasArg;

        return this;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public Parameter isOptional(boolean optional) {
        isOptional = optional;

        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static Parameter build(String name){
        Parameter parameter = new Parameter();
        parameter.setName(name);

        return parameter;
    }
}
