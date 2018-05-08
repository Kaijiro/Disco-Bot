package fr.kaijiro.disco.bot.commands.parameters.exceptions;

public class MissingParameterException extends Exception{

    public MissingParameterException(String message){
        super(message);
    }

    public MissingParameterException(){
        super("");
    }
}
