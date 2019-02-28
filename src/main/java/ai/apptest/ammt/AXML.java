package ai.apptest.ammt;

import ai.apptest.ammt.editor.ManifestEditor;
import ai.apptest.ammt.editor.utils.Pair;
import com.google.devtools.common.options.OptionsParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AXML {

    private static ManifestEditor editor = null;

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: ammt OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),  OptionsParser.HelpVerbosity.LONG));
    }

    private static void loadInput(AXMLOptions options) throws Exception {
        editor = new ManifestEditor(options.getInputFile());
    }

    private static void writeOutput(AXMLOptions options) throws IOException {
        editor.saveAs(options.getOutputFile());

        if (options.verbose) {
            XmlDecompressor xmlDecompressor = new XmlDecompressor();
            System.out.println("Output AndroidManifest.xml\n" + xmlDecompressor.decompressXml(editor.toByteArray()));
        }
    }

    private static void doTagCommand(AXMLOptions options) throws Exception {
        loadInput(options);

        String[] resources = options.getResources();
        if (options.mode.equals(AXMLOptions.MODE_INSERT)) {
            if (resources.length < 4) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];

            List<Pair<String, String>> attrs = new ArrayList<>();
            for (int i = 2; resources.length > i; i+=2) {
                String attr     = resources[i];
                String value    = resources[i+1];

                attrs.add(new Pair<>(attr, value));
            }

            editor.addTag(tag, tagName, attrs);
//            editor.addApptestaiReceiver();

        } else if (options.mode.equals(AXMLOptions.MODE_REMOVE)) {
            if (resources.length != 2) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];

            editor.removeTag(tag, tagName);

        } else {
            throw new Exception("Invalid mode: " + options.mode);
        }

        writeOutput(options);
    }

    private static void doAttrCommand(AXMLOptions options) throws Exception {
        loadInput(options);

        String[] resources = options.getResources();
        if (options.mode.equals(AXMLOptions.MODE_INSERT)) {
            if (resources.length != 4) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];
            String attr     = resources[2];
            String value    = resources[3];

            editor.addAttr(tag, tagName, attr, value);

        } else if (options.mode.equals(AXMLOptions.MODE_REMOVE)) {
            if (resources.length != 3) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];
            String attr     = resources[2];

            editor.removeAttr(tag, tagName, attr);

        } else if (options.mode.equals(AXMLOptions.MODE_MODIFY)) {
            if (resources.length != 4) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];
            String attr     = resources[2];
            String value    = resources[3];

            editor.modifyAttr(tag, tagName, attr, value);

        } else {
            throw new Exception("Invalid mode: " + options.mode);
        }

        writeOutput(options);
    }

    public static void main(String[] args) {
        OptionsParser parser = OptionsParser.newOptionsParser(AXMLOptions.class);

        try {
            parser.parseAndExitUponError(args);
            AXMLOptions options = parser.getOptions(AXMLOptions.class);

            if (options.help) {
                printUsage(parser);
                System.exit(0);
            }

            if (options.getInputFile() == null || !options.getInputFile().exists()) {
                System.out.println("The input AndroidManifest.xml not exist");
                printUsage(parser);
                System.exit(1);
            }

            if (options.getOutputFile() == null) {
                System.out.println("The output file can not be created");
                printUsage(parser);
                System.exit(1);
            }

            if (options.type.equals(AXMLOptions.TYPE_TAG)) {
                doTagCommand(options);
                System.exit(0);
            }

            if (options.type.equals(AXMLOptions.TYPE_ATTR)) {
                doAttrCommand(options);
                System.exit(0);
            }

            throw new Exception("Invalid params");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getLocalizedMessage());
            printUsage(parser);
            System.exit(1);
        }
    }
}
