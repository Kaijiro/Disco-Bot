package fr.kaijiro.disco.bot.test;

import fr.kaijiro.disco.bot.commands.parameters.DiscoCommandParser;
import fr.kaijiro.disco.bot.commands.parameters.Parameter;
import fr.kaijiro.disco.bot.commands.parameters.exceptions.IncorrectValueException;
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

    private DiscoCommandParser parser;

    @Before
    public void resetParamList(){
        this.parser = new DiscoCommandParser();

        this.parameters = new ArrayList<>();
        this.parameters.add(
                Parameter.build("mode")
                        .hasArg(false)
                        .isOptional(false)
        );
        this.parameters.add(
                Parameter.build("param")
                        .hasArg(true)
                        .isOptional(true)
                        .waitedType(Integer.class)
                        .validatedWith(e -> Integer.valueOf(e) > 0 && Integer.valueOf(e) < 100)
        );
    }

    @Test(expected = MissingParameterException.class)
    public void testMandatoryParameters() throws MissingValueException, MissingParameterException, IncorrectValueException {
        // Test mandatory parameter omission
        this.parser.parse("!cmd param=1", this.parameters);
    }

    @Test
    public void testParameterAffectation() throws MissingValueException, MissingParameterException, IncorrectValueException {
        // Test parameter finding
        Map<String, String> parsedParams = this.parser.parse("!cmd test param 1", this.parameters);

        Assert.assertTrue(parsedParams.containsKey("param"));
        Assert.assertEquals("1", parsedParams.get("param"));
    }

    @Test(expected = MissingValueException.class)
    public void testMissingValue() throws MissingValueException, MissingParameterException, IncorrectValueException {
        // Test missing value for parameter
        this.parser.parse("!cmd test param", this.parameters);
    }

    @Test(expected = IncorrectValueException.class)
    public void testIncorrectValue() throws MissingValueException, MissingParameterException, IncorrectValueException {
        // Test that an exception is thrown when the value is not valid
        this.parser.parse("!cmd test param 1000", this.parameters);
    }

    @Test(expected = IncorrectValueException.class)
    public void testIncorrectValueType() throws MissingParameterException, IncorrectValueException, MissingValueException {
        // Test that an exception is thrown when the value type is not valid
        this.parser.parse("!cml test param pouet", this.parameters);
    }
}
