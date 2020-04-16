package fr.kaijiro.disco.bot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.kaijiro.disco.bot.annotations.Command;
import fr.kaijiro.disco.bot.model.ChuckFact;

@Command(value = "!dt", aliases = {})
public class DTCommand extends AbstractBotCommand {

    public static final String CHUCK_NORRIS_FACTS_URL = "http://www.chucknorrisfacts.fr/api/get?data=nb:1;type:txt;tri:alea";

    @Override
    public void execute(Map<String, String> parameters) {
        this.respond(this.getFactFromApi().replace("Chuck Norris", "DayTay"));
    }

    @Override
    public void formatHelp() {
        this.respond("C'est une commande de merde");
    }

    private String getFactFromApi() {
        String fact;

        try {
            URL url = new URL(CHUCK_NORRIS_FACTS_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");

            String httpResponseContent = this.getHttpResponseContent(connection);
            fact = this.getChuckFact(httpResponseContent);

            connection.disconnect();
        } catch (Exception e) {
            this.logger.debug(e.getMessage(),e);
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