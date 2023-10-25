package gov.cdc.dataingestion.commands;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@CommandLine.Command(name = "validatehl7", mixinStandardHelpOptions = true, description = "Validates the provided HL7 document.")
public class ValidateHL7 implements Runnable{

    @CommandLine.Option(names = {"--hl7-file"}, description = "HL7 file name with fully qualified path", interactive = true, echo = true, required = true)
    String hl7FilePath;

    private final String newLine = "\n";
    private final String newLineWithCarrier = "\n\r";
    private final String carrier = "\r";

    // this is the support hl7 structure
    private final String supportedHL7version = "2.5.1";
    private final String supportedHL7version231 = "2.3.1";
    private static final String exMessage = "Invalid Message ";

    @Override
    public void run() {

        StringBuilder hl7Message = readHL7File(hl7FilePath);

        String dummymessage = hl7Message.toString();

        String validMessage = "MSH|^~\\&|MedSeries|CAISI_1-2|PLS|3910|200903230934||ADT^A31^ADT_A05|75535037-1237815294895|P^T|2.4\r\n"
                + "EVN|A31|200903230934\r\n"
                + "PID|1||29^^CAISI_1-2^PI~\"\"||Test300^Leticia^^^^^L||19770202|M||||||||||||||||||||||";

        String message = "MSH|^~\\&|oct13test^^|LABCORP^34D0655059^CLIA|ALDOH^^|AL^^|202305251105||ORU^R01^ORU_R01|202305221034-A|P^|2.5.1|\n" +
                "PID|1||28164102670^^^LABCORP Birmingham&01D0301471&CLIA^U^^^^^^~441487312^^^&2.16.840.1.113883.5.1008&NI^PT^^^^^^~234111222^^^SSA&2.16.840.1.113883.4.1&ISO^SS^^^^^^||datateam&&&&^LinkLogic^^^^^^||20230609000000|M||U^^^^^^^^|25 Adams St^^FAIRFIELD^GA^30342^||^^^^^205^2714823^^^^^|||||||||U^^^^^^^^|||||||||||||||||||\n" +
                "ORC|RE||34411541099348||||||||||||||||||Simon-Williamson Cl-Princeton^^^^^^^^^|832 Princeton Ave SW^^Birmingham^GA^30342^|^^^^^205^2068375^^^^^|832 Princeton Ave SW^^Birmingham^GA^30342^||||||||\n" +
                "OBR|1||34411541099348|^^^test abc^L^^^^|||20230522104500|||||||||1710927256^STEWART&&&&^THULANI^^^^MD^^^^^^NPI^^^^^^^^^^^^|^^^^^205^2068375^^^^^||||||||F||||||||||||||||||||||||||\n" +
                "OBX|1|CE|13502-0^Lyme disease^LN^^^^^^||10828004^Positive^SNM^||Negative|A^^^^^^^^|||F||||01D0301471^LABCORP Birmingham^CLIA^^^^^^||||20230522104516||||LABCORP Birmingham^^^^^CLIA&&^^^^01D0301471|832 Princeton Ave SW^^^^^|||";
        if (message.contains(newLineWithCarrier) || message.contains(carrier) || message.contains(newLine)) {
            if (message.contains(newLineWithCarrier)) {
                message = message.replaceAll(newLineWithCarrier, carrier);
            }
            else if (message.contains(newLine)) {
                message = message.replaceAll(newLine, carrier);
            }
        } else {
            if (message.contains("\\n")) {
                message = message.replaceAll("\\\\n",carrier);
            }
            else if (message.contains("\\r")) {
                message = message.replaceAll("\\\\r",carrier);
            }
        }

        // make sure message only contain `\` on MSH
        message = message.replaceAll("\\\\+", "\\\\");

        HapiContext hapiContext = new DefaultHapiContext();
//        hapiContext.setModelClassFactory(new DefaultModelClassFactory());
//        hapiContext.setValidationContext(ValidationContextFactory.defaultValidation());
        hapiContext.setValidationContext(new NoValidation());

        PipeParser parser = hapiContext.getPipeParser();

        try {
            System.out.println("Parsing the following message..." + message);

            Message hapiMessage = parser.parse(message);
            System.out.println("ValParsed successfully..." + hapiMessage);

            //hapiContext.getMessageValidator().validate(message);
        } catch (HL7Exception e) {
            System.err.println("Something went wrong..." + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    static StringBuilder readHL7File(String hl7FilePath) {
        StringBuilder hl7Message = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(hl7FilePath))) {
            String line;
            while((line = reader.readLine()) != null) {
                hl7Message.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return hl7Message;
    }
}
