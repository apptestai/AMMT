package ai.apptest.ammt;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

import java.io.File;

public class AXMLOptions extends OptionsBase {
    public static final String TYPE_ATTR    = "attr";
    public static final String TYPE_TAG     = "tag";

    public static final String MODE_INSERT  = "insert";
    public static final String MODE_REMOVE  = "remove";
    public static final String MODE_MODIFY  = "modify";

    @Option(
            name = "help",
            abbrev = 'h',
            help = "print usage",
            defaultValue = "false"
    )
    public boolean help;

    @Option(
            name = "verbose",
            abbrev = 'v',
            help = "verbose mode",
            defaultValue = "false"
    )
    public boolean verbose;

    ////////////////////////////////////////
    @Option(
            name = "type",
                abbrev = 't',
            help = "tag|attr",
            defaultValue = "attr"
    )
    public String type;

    @Option(
            name = "mode",
                abbrev = 'm',
            help = "insert|remove|modify",
            defaultValue = "modify"
    )
    public String mode;

    @Option(
            name = "resources",
            abbrev = 'r',
            help = "seperated by ','(e.g., -t attr -m insert -r application,package,debuggable,true -i AndroidManifest.xml)",
            defaultValue = ""
    )
    public String resources;

    @Option(
            name = "input",
            abbrev = 'i',
            help = "input AndroidManifest.xml",
            defaultValue = ""
    )
    public String input;

    @Option(
            name = "output",
            abbrev = 'o',
            help = "output AndroidManifest.xml",
            defaultValue = "AndroidManifest_out.xml"
    )
    public String output;

    public File getInputFile() {
        if (this.input.trim().isEmpty()) {
            return null;
        }

        return new File(this.input.trim());
    }

    public File getOutputFile() {
        if (this.output.trim().isEmpty()) {
            return null;
        }

        return new File(this.output.trim());
    }

    public String[] getResources() {
        return this.resources.trim().split("[ \t]*,[ \t]*");
    }
}
