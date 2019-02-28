package ai.apptest.ammt;

import cn.wjdiankong.main.ParserChunkUtils;
import cn.wjdiankong.main.XmlEditor;
import com.google.devtools.common.options.OptionsParser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class AXML2 {

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: ammt OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),  OptionsParser.HelpVerbosity.LONG));
    }

    private static void loadInput(AXMLOptions options) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try{
            fis = new FileInputStream(options.getInputFile());
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len=fis.read(buffer)) != -1){
                bos.write(buffer, 0, len);
            }
            ParserChunkUtils.xmlStruct.byteSrc = bos.toByteArray();
        }catch(Exception e){
            System.out.println("parse xml error:"+e.toString());
        }finally{
            try{
                fis.close();
                bos.close();
            }catch(Exception e){
            }
        }
    }

    private static void writeOutput(AXMLOptions options) throws IOException {
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(options.getOutputFile());
            fos.write(ParserChunkUtils.xmlStruct.byteSrc);
            fos.close();
        }catch(Exception e){
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (options.verbose) {
            XmlDecompressor xmlDecompressor = new XmlDecompressor();
            System.out.println("Output AndroidManifest.xml\n" + xmlDecompressor.decompressXml(ParserChunkUtils.xmlStruct.byteSrc));
        }
    }

    private static void doTagCommand(AXMLOptions options) throws Exception {
        loadInput(options);

        String[] resources = options.getResources();
        if (options.mode.equals(AXMLOptions.MODE_INSERT)) {
            if (resources.length != 1) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String subsetXml = resources[0];
            XmlEditor.addTag(subsetXml);

        } else if (options.mode.equals(AXMLOptions.MODE_REMOVE)) {
            if (resources.length != 2) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];

            XmlEditor.removeTag(tag, tagName);

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

            XmlEditor.addAttr(tag, tagName, attr, value);

        } else if (options.mode.equals(AXMLOptions.MODE_REMOVE)) {
            if (resources.length != 3) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];
            String attr     = resources[2];

            XmlEditor.removeAttr(tag, tagName, attr);

        } else if (options.mode.equals(AXMLOptions.MODE_MODIFY)) {
            if (resources.length != 4) {
                throw new Exception("Invalid resources: " + Arrays.toString(resources));
            }

            String tag      = resources[0];
            String tagName  = resources[1];
            String attr     = resources[2];
            String value    = resources[3];

            XmlEditor.modifyAttr(tag, tagName, attr, value);

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
                System.exit(1);
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
            System.err.println(e.getLocalizedMessage());
            printUsage(parser);
            System.exit(1);
        }
    }
}
