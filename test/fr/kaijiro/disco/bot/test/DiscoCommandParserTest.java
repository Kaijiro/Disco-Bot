package fr.kaijiro.disco.bot.test;

import fr.kaijiro.disco.bot.commands.parameters.DiscoCommandParser;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingParameterException;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.MissingValueException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscoCommandParserTest {

    private List<Parameter> parameters;

    @Before
    public void resetParamList(){
        this.parameters = new ArrayList<>();
        this.parameters.add(Parameter.build("mode").hasArg(false).isOptional(false));
        this.parameters.add(Parameter.build("param").hasArg(true).isOptional(true));
    }

    @Test(expected = MissingParameterException.class)
    public void testMandatoryParameters() throws MissingValueException, MissingParameterException {
        DiscoCommandParser parser = new DiscoCommandParser();

        // Test mandatory parameter omission
        parser.parse("!cmd param=1", this.parameters);
    }

    @Test
    public void testParameterAffectation() throws MissingValueException, MissingParameterException {
        DiscoCommandParser parser = new DiscoCommandParser();

        // Test parameter finding
        Map<String, String> parsedParams = parser.parse("!cmd test param 1", this.parameters);

        Assert.assertTrue(parsedParams.containsKey("param"));
        Assert.assertEquals("1", parsedParams.get("param"));
    }

    @Test(expected = MissingValueException.class)
    public void testMissingValue() throws MissingValueException, MissingParameterException {
        DiscoCommandParser parser = new DiscoCommandParser();

        // Test missing value for parameter
        parser.parse("!cmd test param", this.parameters);
    }
}
