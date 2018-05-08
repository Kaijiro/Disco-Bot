package fr.kaijiro.disco.bot.commands.parameters.exceptions;

public class MissingValueException extends Exception {

    public MissingValueException(String message){
        super(message);
    }

    public MissingValueException(){
        super("");
    }
}
