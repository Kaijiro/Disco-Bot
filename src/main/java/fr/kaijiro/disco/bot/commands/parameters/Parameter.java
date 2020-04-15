package fr.kaijiro.disco.bot.commands.parameters;

import java.util.function.Predicate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Parameter {
    private String name;

    private boolean hasArg = false;

    private boolean isOptional = true;

    private String defaultValue = "";

    private Predicate<String> validator;

    private Class<?> waitedType = String.class;

    public String getName() {
        return this.name;
    }

    public Parameter setName(String name) {
        this.name = name;

        return this;
    }

    public boolean hasArg() {
        return this.hasArg;
    }

    public Parameter hasArg(boolean hasArg) {
        this.hasArg = hasArg;

        return this;
    }

    public boolean isOptional() {
        return this.isOptional;
    }

    public Parameter isOptional(boolean optional) {
        this.isOptional = optional;

        return this;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Parameter validatedWith(Predicate<String> validator) {
        this.validator = validator;

        return this;
    }

    public Predicate<String> getValidator() {
        return this.validator;
    }

    public Class<?> getWaitedType() {
        return this.waitedType;
    }

    public Parameter waitedType(Class<?> waitedType) {
        this.waitedType = waitedType;

        return this;
    }

    public static Parameter build(String name){
        Parameter parameter = new Parameter();
        parameter.setName(name);

        return parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        return new EqualsBuilder()
                .append(this.name, parameter.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.name)
                .toHashCode();
    }
}
