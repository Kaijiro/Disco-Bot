package fr.kaijiro.disco.bot.commands.parameters.exceptions;

public class IncorrectValueException extends Exception {

    public IncorrectValueException(String message){
        super(message);
    }

    public IncorrectValueException(){
        super("");
    }
}
