package fr.kaijiro.disco.bot.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.kaijiro.disco.bot.annotations.Command;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Command(value = "!dt", aliases = {})
public class DTCommand extends AbstractBotCommand {

    @Override
    public void execute(Map<String, String> parameters) {
        this.respond(getFactFromApi().replace("Chuck Norris", "DayTay"));
    }

    @Override
    public void formatHelp() {
        this.respond("C'est une commande de merde");
    }

    private String getFactFromApi() {
        String location = "https://www.chucknorrisfacts.fr/api/get?data=nb:1;type:txt;tri:alea";
        String fact = null;

        try {
            URL url = new URL(location);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");

            String httpResponseContent = getHttpResponseContent(connection);
            fact = getChuckFact(httpResponseContent);

            connection.disconnect();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return "Erreur lors de la récupération du fact";
        }

        if(fact == null) {
            return "Pas trouvé...";
        }

        return fact;
    }

    private String getChuckFact(String httpResponseContent) throws com.fasterxml.jackson.core.JsonProcessingException {
        String fact;
        ObjectMapper mapper = new ObjectMapper();
        httpResponseContent = httpResponseContent.replace("[", "");
        httpResponseContent = httpResponseContent.replace("]", "");
        ChuckFact chuckFact = mapper.readValue(httpResponseContent, ChuckFact.class);
        fact = chuckFact.getFact();
        return fact;
    }

    private String getHttpResponseContent(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return StringEscapeUtils.unescapeHtml4(content.toString());
    }
}

class ChuckFact {
    private String id;
    private String fact;
    private Date date;
    private int vote;
    private int points;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

// https://www.chucknorrisfacts.fr/api/get?data=nb:1;type:txt