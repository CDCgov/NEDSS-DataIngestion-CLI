package gov.cdc.dataingestion;

import gov.cdc.dataingestion.commands.InjectHL7;
import gov.cdc.dataingestion.commands.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class DataIngestionCLITest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outStream));
    }

    @Test
    void testMainExitCodeMissingArguments() {
        String[] args = { "status" };

        int exitCode = new CommandLine(new DataIngestionCLI()).execute(args);

        assertEquals(2, exitCode);
    }

    @Test
    void testMainTokenSubCommand() {
        CommandLine cmd = new CommandLine(new DataIngestionCLI());
        CommandLine subCommand = cmd.getSubcommands().get("token");

        assertNotNull(subCommand);
        assertEquals(TokenGenerator.class, subCommand.getCommand().getClass());
    }

    @Test
    void testMainInjectHL7SubCommand() {
        CommandLine cmd = new CommandLine(new DataIngestionCLI());
        CommandLine subCommand = cmd.getSubcommands().get("injecthl7");

        assertNotNull(subCommand);
        assertEquals(InjectHL7.class, subCommand.getCommand().getClass());
    }
}
