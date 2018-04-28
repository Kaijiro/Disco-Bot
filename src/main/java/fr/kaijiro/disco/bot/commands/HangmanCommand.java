package fr.kaijiro.disco.bot.commands;

import fr.kaijiro.disco.bot.annotations.Command;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command
public class HangmanCommand implements IListener<MessageReceivedEvent> {

    private static Logger logger = LogManager.getLogger(HangmanCommand.class);

    public static final String SPACE = " ";

    private static final String COMMAND = "!hangman";

    private static final String COMMAND_START = "start";

    private static final String COMMAND_STATUS = "status";

    private static final String COMMAND_TRY = "try";

    private static final String COMMAND_GUESS = "guess";

    private String wordToGuess = "";

    private int livesLeft = -1;

    private static Set<String> triedLetters = new HashSet<>();

    private static boolean gameStarted = false;

    public void handle(MessageReceivedEvent event) {

        String message = event.getMessage().getContent().toLowerCase();
        if(message.startsWith(COMMAND)){
            String[] args = message.split(" ");


            if(args.length < 2) {
                handleErrorNbArgs(event);
                return;
            }

            if(!args[1].equals(COMMAND_START) && !gameStarted) {
                handleStatusOption(event);
            }

            switch(args[1]) {
                case COMMAND_START :
                    handleStartOption(event);
                    break;

                case COMMAND_STATUS :
                    handleStatusOption(event);
                    break;

                case COMMAND_TRY:
                    if(args.length < 3) {
                        handleErrorNbArgs(event);
                        return;
                    } else {
                        handleTryOption(event, args[2]);
                    }
                    break;
                case COMMAND_GUESS:
                    handleGuessOption(event, args);
                    break;
                default :
                    handleErrorNbArgs(event);
                    return;
            }
        }
    }

    private void handleStartOption(MessageReceivedEvent event) {
        MessageBuilder builder = new MessageBuilder(event.getClient());
        if(gameStarted) {
            builder.withContent("A game is already started !\n" +
                    "Type " + COMMAND_STATUS + " to display letters tryed and current game status.")
                    .withChannel(event.getMessage().getChannel())
                    .send();
        } else {
            do {
                wordToGuess = getNewWordToGuess();
            } while(wordToGuess.contains("%"));
            triedLetters.clear();
            triedLetters.add(SPACE);
            gameStarted = true;
            livesLeft = 5;
            builder.withContent("A new game has been started ! \n\n" +
                    getGameStatus())
                    .withChannel(event.getMessage().getChannel())
                    .send();
        }
    }

    private void handleStatusOption(MessageReceivedEvent event) {
        MessageBuilder builder = new MessageBuilder(event.getClient());
        String content;

        if(gameStarted) {
            content = getGameStatus();
        } else {
            content = "No game started for now, start one with : \n" +
                    "`" + COMMAND + " " + COMMAND_START + "`";
        }

        builder.withContent(content)
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    private void handleTryOption(MessageReceivedEvent event, String try_) {
        MessageBuilder builder = new MessageBuilder(event.getClient());

        Pattern pattern = Pattern.compile("^[a-z1-9]{1}$");
        Matcher matcher = pattern.matcher(try_);

        String content;

        if(matcher.find()) { // It is a letter or a number
            content = "You tried : " + try_;
            if(triedLetters.contains(try_)) { // If user already tried this letter/number
                content += "\n" + "Unfortunately it was already used. -1 live";
                livesLeft -= 1;
            } else if (!wordToGuess.contains(try_)) { // If the letter/number is not in the word to try_
                content += "\n" + "Unfortunatly it is not in the word to guess !";
                triedLetters.add(try_);
                livesLeft -= 1;

            } else { // If the letter/number is in the word to try_
                triedLetters.add(try_);
            }
            content += "\n" + getGameStatus();
        } else { // Not a letter or a number
            livesLeft -= 1;
            content = "Error, expecting a letter or a number ! -1 live \n" + getGameStatus();
        }

        if(testIfFoundWord()) { // Check if user found the word
            content += "\n" + "Congratulation ! You found the word " + wordToGuess;
            gameStarted = false;
        }

        builder.withContent(content)
                .withChannel(event.getMessage().getChannel())
                .send();

        if(livesLeft == 0) { // Game lost - No lives left
            handleGameLost(event);
        }
    }

    private void handleGuessOption(MessageReceivedEvent event, String[] args) {
        MessageBuilder builder = new MessageBuilder(event.getClient());

        String guess = "";
        for (int i = 2; i < args.length; i++) {
            guess += args[i] + SPACE;
        }
        guess = guess.trim();

        String content;
        if(wordToGuess.equals(guess)) {
            content = "Congratulations, you guessed the word " + wordToGuess;
            gameStarted = false;
        } else {
            content = "Humm no, the word to guess is not " + guess + "... -1 live";
            livesLeft -= 1;
        }

        builder.withContent(content)
                .withChannel(event.getMessage().getChannel())
                .send();

        if(livesLeft == 0) { // Game lost - No lives left
            handleGameLost(event);
        }
    }

    private void handleErrorNbArgs(MessageReceivedEvent event) {
        MessageBuilder builder = new MessageBuilder(event.getClient());
        builder.withContent(":warning:  Error, to play a Hangman game, I wait 2 parameters ! :warning: \n\n" +
                "`" + COMMAND + " ( " +COMMAND_START + " | " + COMMAND_STATUS + " | " + COMMAND_TRY + " + 1 letter | " + COMMAND_GUESS + " + your guess )`\n\n" +
                "Try again ! :wink:")
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    private void handleGameLost(MessageReceivedEvent event) {
        MessageBuilder builder = new MessageBuilder(event.getClient());

        String content = "No more life left !\n" +
                "You have not guessed the word " + wordToGuess + "\n" +
                "You used those letters : ";
        for (String s : triedLetters) {
            content += s + " ";
        }
        content += "\n" + "Start a new game with `" + COMMAND + " " + COMMAND_START + "` !";

        gameStarted = false;

        builder.withContent(content)
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    private boolean testIfFoundWord() {
        for (String s : wordToGuess.split("")) {
            if(!triedLetters.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public String getNewWordToGuess() {
        String out = "test";
        try {
            out = correctWord(sendGet());
        } catch(Exception e) {
            logger.error("Error while getting the new word to guess", e);
        }

        logger.debug("The word to guess is : " + out);

        return out;
    }

    public String getGameStatus() {
        return "Live" + (livesLeft>1?"s":"") + " left : " + livesLeft + "\n" +
                getWordToGuessForDisplay() ;
    }

    public String getWordToGuessForDisplay() {
        String out = "";

        for (String s : wordToGuess.split("")) {
            if(triedLetters.contains(s)) {
                out += s;
            } else {
                out += "#";
            }
            out += " ";
        }

        return out.toUpperCase();
    }

    private String sendGet() throws Exception {

        String location = "https://fr.wikipedia.org/wiki/Spécial:Page_au_hasard";

        URL url = new URL(location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        String redirectLocation = connection.getHeaderField("Location");

        String out = redirectLocation.replace("https://fr.wikipedia.org/wiki/", "");
        logger.debug("Page wiki : " + out);
        return out;

    }

    private static String correctWord(String s)
    {
        s = s.toLowerCase();
        s = StringUtils.stripAccents(s);
        s = s.replaceAll("_", SPACE);
        s = s.replaceAll("\\(.*?\\)", "");
        s = s.trim();
        return s;
    }
}
