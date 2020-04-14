package fr.kaijiro.disco.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import fr.kaijiro.disco.bot.annotations.Command;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(value = "!hangman", aliases = {"!pendu"})
public class HangmanCommand extends AbstractBotCommand {

    public static final String SPACE = " ";

    private static final String COMMAND_START = "start";

    private static final String COMMAND_STATUS = "status";

    private static final String COMMAND_TRY = "try";

    private static final String COMMAND_GUESS = "guess";

    private String wordToGuess = "";

    private int livesLeft = -1;

    private static final Set<String> triedLetters = new HashSet<>();

    private static boolean gameStarted = false;

    @Override
    public void execute(Map<String, String> parameters) {

        String message = this.event.getMessage().getContent().orElse("").toLowerCase();
        String[] args = message.split(" ");


        if (args.length < 2) {
            this.handleErrorNbArgs(this.event);
            return;
        }

        if (!args[1].equals(COMMAND_START) && !gameStarted) {
            this.handleStatusOption(this.event);
        }

        switch (args[1]) {
            case COMMAND_START:
                this.handleStartOption(this.event);
                break;

            case COMMAND_STATUS:
                this.handleStatusOption(this.event);
                break;

            case COMMAND_TRY:
                if (args.length < 3) {
                    this.handleErrorNbArgs(this.event);
                    return;
                } else {
                    this.handleTryOption(this.event, args[2]);
                }
                break;
            case COMMAND_GUESS:
                this.handleGuessOption(this.event, args);
                break;
            default:
                this.handleErrorNbArgs(this.event);
                return;
        }
    }

    @Override
    public void formatHelp() {
    }

    private void handleStartOption(MessageCreateEvent event) {
        if (gameStarted) {
            this.respond("A game is already started !\n" +
                    "Type " + COMMAND_STATUS + " to display letters tryed and current game status.");
        } else {
            do {
                this.wordToGuess = this.getNewWordToGuess();
            } while (this.wordToGuess.contains("%"));
            triedLetters.clear();
            triedLetters.add(SPACE);
            gameStarted = true;
            this.livesLeft = 5;
            this.respond("A new game has been started ! \n\n" +
                    this.getGameStatus());
        }
    }

    private void handleStatusOption(MessageCreateEvent event) {
        String content;

        if (gameStarted) {
            content = this.getGameStatus();
        } else {
            content = "No game started for now, start one with : \n" +
                    "`" + this.getCommandNameShort() + " " + COMMAND_START + "`";
        }

        this.respond(content);
    }

    private void handleTryOption(MessageCreateEvent event, String try_) {
        Pattern pattern = Pattern.compile("^[a-z1-9]$");
        Matcher matcher = pattern.matcher(try_);

        String content;

        if (matcher.find()) { // It is a letter or a number
            content = "You tried : " + try_;
            if (triedLetters.contains(try_)) { // If user already tried this letter/number
                content += "\n" + "Unfortunately it was already used. -1 live";
                this.livesLeft -= 1;
            } else if (!this.wordToGuess.contains(try_)) { // If the letter/number is not in the word to try_
                content += "\n" + "Unfortunatly it is not in the word to guess !";
                triedLetters.add(try_);
                this.livesLeft -= 1;

            } else { // If the letter/number is in the word to try_
                triedLetters.add(try_);
            }
            content += "\n" + this.getGameStatus();
        } else { // Not a letter or a number
            this.livesLeft -= 1;
            content = "Error, expecting a letter or a number ! -1 live \n" + this.getGameStatus();
        }

        if (this.testIfFoundWord()) { // Check if user found the word
            content += "\n" + "Congratulation ! You found the word " + this.wordToGuess;
            gameStarted = false;
        }

        this.respond(content);

        if (this.livesLeft == 0) { // Game lost - No lives left
            this.handleGameLost(event);
        }
    }

    private void handleGuessOption(MessageCreateEvent event, String[] args) {
        String guess = "";
        for (int i = 2; i < args.length; i++) {
            guess += args[i] + SPACE;
        }
        guess = guess.trim();

        String content;
        if (this.wordToGuess.equals(guess)) {
            content = "Congratulations, you guessed the word " + this.wordToGuess;
            gameStarted = false;
        } else {
            content = "Humm no, the word to guess is not " + guess + "... -1 live";
            this.livesLeft -= 1;
        }

        this.respond(content);

        if (this.livesLeft == 0) { // Game lost - No lives left
            this.handleGameLost(event);
        }
    }

    private void handleErrorNbArgs(MessageCreateEvent event) {
        this.respond(":warning:  Error, to play a Hangman game, I wait 2 parameters ! :warning: \n\n" +
                "`" + this.getCommandNameShort() + " ( " + COMMAND_START + " | " + COMMAND_STATUS + " | " + COMMAND_TRY + " + 1 letter | " + COMMAND_GUESS + " + your guess )`\n\n" +
                "Try again ! :wink:");
    }

    private void handleGameLost(MessageCreateEvent event) {
        String content = "No more life left !\n" +
                "You have not guessed the word " + this.wordToGuess + "\n" +
                "You used those letters : ";
        for (String s : triedLetters) {
            content += s + " ";
        }
        content += "\n" + "Start a new game with `" + this.getCommandNameShort() + " " + COMMAND_START + "` !";

        gameStarted = false;

        this.respond(content);
    }

    private boolean testIfFoundWord() {
        for (String s : this.wordToGuess.split("")) {
            if (!triedLetters.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public String getNewWordToGuess() {
        String out = "test";
        try {
            out = correctWord(this.sendGet());
        } catch (Exception e) {
            logger.error("Error while getting the new word to guess", e);
        }

        logger.debug("The word to guess is : " + out);

        return out;
    }

    public String getGameStatus() {
        return "Live" + (this.livesLeft > 1 ? "s" : "") + " left : " + this.livesLeft + "\n" +
                this.getWordToGuessForDisplay();
    }

    public String getWordToGuessForDisplay() {
        String out = "";

        for (String s : this.wordToGuess.split("")) {
            if (triedLetters.contains(s)) {
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

    private static String correctWord(String s) {
        s = s.toLowerCase();
        s = StringUtils.stripAccents(s);
        s = s.replaceAll("_", SPACE);
        s = s.replaceAll("\\(.*?\\)", "");
        s = s.trim();
        return s;
    }
}
