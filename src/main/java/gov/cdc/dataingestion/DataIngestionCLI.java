package gov.cdc.dataingestion;

import gov.cdc.dataingestion.commands.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "DataIngestionCLI",
        subcommands = { TokenGenerator.class, InjectHL7.class, ReportStatus.class, Hl7Validation.class, DeadLetterMessages.class },
        mixinStandardHelpOptions = true, version = "1.0",
        description = "Command Line Interface to connect to NBS DI Service.")
class DataIngestionCLI {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DataIngestionCLI()).execute(args);
        System.exit(exitCode);
    }
}
