package fr.kaijiro.disco.bot.commands;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import fr.kaijiro.disco.bot.annotations.Command;

@Command(value = "!hangman", aliases = {"!pendu", "!hg"})
public class HangmanCommand extends AbstractBotCommand {

    public static final String SPACE = " ";

    private static final String COMMAND_START = "start";

    private static final String COMMAND_STATUS = "status";

    private static final String COMMAND_TRY = "try";

    private static final String COMMAND_GUESS = "guess";

    private static final String COMMAND_FORFEIT = "forfeit";
    private static final String COMMAND_FORFAIT = "forfait";
    private static final String COMMAND_FF = "ff";

    private String wordToGuess = "";

    private int livesLeft = -1;

    private static final Set<String> triedLetters = new HashSet<>();

    private static boolean gameStarted = false;

    @Override
    public void execute(Map<String, String> parameters) {

        String message = this.event.getMessage().getContent().orElse("").toLowerCase();
        String[] args = message.split(" ");

        if (args.length < 2) {
            this.handleErrorNbArgs();
            return;
        }

        if (!args[1].equals(COMMAND_START) && !gameStarted && !args[1].equals(COMMAND_STATUS)) {
            this.handleStatusOption();
        }

        switch (args[1]) {
            case COMMAND_START:
                this.handleStartOption();
                break;

            case COMMAND_STATUS:
                this.handleStatusOption();
                break;

            case COMMAND_TRY:
                if (args.length < 3) {
                    this.handleErrorNbArgs();
                    return;
                } else {
                    this.handleTryOption(Arrays.copyOfRange(args,2, args.length));
                }
                break;
            case COMMAND_GUESS:
                this.handleGuessOption(args);
                break;
            case COMMAND_FORFEIT:
            case COMMAND_FORFAIT:
            case COMMAND_FF:
                this.handleForfait();
                break;
            default:
                this.handleErrorNbArgs();
                return;
        }
    }

    @Override
    public void formatHelp() {
        this.respond("`" + this.getCommandNameShort() + " ( " + COMMAND_START + " | " + COMMAND_STATUS + " | " + COMMAND_FORFEIT + " | " + COMMAND_TRY + " + 1 letter | " + COMMAND_GUESS + " + your guess )`");
    }

    private void handleStartOption() {
        if (gameStarted) {
            this.respond("A game is already started !\n" +
                    "Type " + COMMAND_STATUS + " to display current game status.");
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

    private void handleStatusOption() {
        String content;

        if (gameStarted) {
            content = this.getGameStatus();
        } else {
            content = "No game started for now, start one with : \n" +
                    "`" + this.getCommandNameShort() + " " + COMMAND_START + "`";
        }

        this.respond(content);
    }

    private void handleTryOption(String... tentatives) {
        if(gameStarted) {
            Pattern pattern = Pattern.compile("^[a-z1-9]$");

            for (String tentative : tentatives) {
                Matcher matcher = pattern.matcher(tentative);

                String content;

                if (matcher.find()) { // It is a letter or a number
                    content = "You tried : " + tentative;
                    if (triedLetters.contains(tentative)) { // If user already tried this letter/number
                        content += "\n" + "Unfortunately it was already used. -1 live";
                        this.livesLeft -= 1;
                    } else if (!this.wordToGuess.contains(tentative)) { // If the letter/number is not in the word to try_
                        content += "\n" + "Unfortunatly it is not in the word to guess !";
                        triedLetters.add(tentative);
                        this.livesLeft -= 1;

                    } else { // If the letter/number is in the word to try_
                        triedLetters.add(tentative);
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
                    this.handleGameLost();
                }

            }
        }
    }

    private void handleGuessOption(String[] args) {
        if(gameStarted) {
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
                this.handleGameLost();
            }
        }
    }

    private void handleErrorNbArgs() {
        this.respond(":warning:  Error, to play a Hangman game, I wait 2 parameters ! :warning: \n\n" +
                "`" + this.getCommandNameShort() + " ( " + COMMAND_START + " | " + COMMAND_STATUS + " | " + COMMAND_FORFEIT + " | " + COMMAND_TRY + " + 1 letter | " + COMMAND_GUESS + " + your guess )`\n\n" +
                "Try again ! :wink:");
    }

    private void handleGameLost() {
        String content = "No more life left !\n" +
                "You have not guessed the word **" + this.wordToGuess + "**\n" +
                "You used those letters : ";
        for (String s : triedLetters) {
            content += s + " ";
        }
        content += "\n" + "Start a new game with `" + this.getCommandNameShort() + " " + COMMAND_START + "` !";

        gameStarted = false;

        this.respond(content);
    }

    private void handleForfait() {
        if(gameStarted) {
            String content = "You forfeited...\n" +
                    "The word was **" + this.wordToGuess + "**\n" +
                    "You used those letters : ";
            for (String s : triedLetters) {
                content += s + " ";
            }
            content += "\n" + "Start a new game with `" + this.getCommandNameShort() + " " + COMMAND_START + "` !";

            gameStarted = false;

            this.respond(content);
        }
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
            this.logger.error("Error while getting the new word to guess", e);
        }

		this.logger.debug("The word to guess is : {}", out);

        return out;
    }

    public String getGameStatus() {
        return "Live" + (this.livesLeft > 1 ? "s" : "") + " left : " + StringUtils.repeat(":heart: ", this.livesLeft) + "\n" +
                this.getWordToGuessForDisplay();
    }

    public String getWordToGuessForDisplay() {
        StringBuilder out = new StringBuilder();

        for (String s : this.wordToGuess.split("")) {
            if (triedLetters.contains(s)) {
				out.append(s);
            } else {
				out.append("#");
            }
            out.append(" ");
        }

        return out.toString().toUpperCase();
    }

    private String sendGet() throws Exception {
        String location = "https://fr.wikipedia.org/wiki/Spécial:Page_au_hasard";

        URL url = new URL(location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        String redirectLocation = connection.getHeaderField("Location");

        String out = redirectLocation.replace("https://fr.wikipedia.org/wiki/", "");
        this.logger.debug("Page wiki : {}", out);
        return out;
    }

    private static String correctWord(String s) {
        s = s.toLowerCase();
        s = StringUtils.stripAccents(s);
        s = s.replace("_", SPACE);
        s = s.replace("\\(.*?\\)", "");
        s = s.trim();
        return s;
    }
}
