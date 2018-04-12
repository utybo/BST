/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.OutputStreamToOutputAndPrint;

/**
 * OpenBST is an open source implementation of the BST language that aims to be
 * fully compatible with every single feature of BST.
 * <p>
 * This class is both the main class and the main JFrame.
 *
 * @author utybo
 *
 */
public class OpenBST
{
    /**
     * Version number of OpenBST
     */
    public static final String VERSION;
    static
    {
        String s = OpenBST.class.getPackage().getImplementationVersion();
        if(s == null)
        {
            VERSION = "<unknown version>";
        }
        else
        {
            VERSION = s;
        }
    }

    public static final Logger LOG;
    private static ByteArrayOutputStream logOutput;
    static
    {
        logOutput = new ByteArrayOutputStream();

        PrintStream sysout = System.out;
        OutputStreamToOutputAndPrint newout = new OutputStreamToOutputAndPrint(logOutput, sysout);
        PrintStream ps = new PrintStream(newout);
        System.setOut(ps);

        PrintStream syserr = System.err;
        OutputStreamToOutputAndPrint newerr = new OutputStreamToOutputAndPrint(logOutput, syserr);
        PrintStream pserr = new PrintStream(newerr);
        System.setErr(pserr);

        LOG = LogManager.getLogger("OpenBST");
    }

    // --- IMAGES ---

    /**
     * Launch OpenBST
     *
     * @param args
     *            Arguments. The first argument is the language code to be used
     */
    public static void main(final String[] args)
    {

        LOG.info("OpenBST version " + VERSION + ", part of the BST project");
        LOG.trace("[ INIT ]");
        LOG.trace("Loading language files");
        loadLang(args.length > 0 ? args[0] : null);

        LOG.trace("Initializing JavaFX");
        // Necessary - because we are killing Scenes all the time with WebViews in NodePanels,
        // JFX may think we just ended our application.
        // OpenBST exits with a dirty System.exit() anyway.
        Platform.setImplicitExit(false);
        new JFXPanel();

        LOG.trace("Applying Look and Feel");
        OpenBSTGUI.initializeLaF();

        LOG.info("Loading icons...");
        long timeAtIconStart = System.currentTimeMillis();
        Icons.load();
        LOG.info("Time taken to load icons : " + (System.currentTimeMillis() - timeAtIconStart));

        LOG.trace("Fixing text scaling");
        VisualsUtils.fixTextFontScaling();

        LOG.trace("Launching app...");
        OpenBSTGUI.launch();
    }

    /**
     * Load the default language (which should be English) as well as the user's
     * language. We avoid loading all the language files to avoid having our RAM
     * usage blowing up.
     *
     * @param userCustomLanguage
     *            The language to use in the application, which must be one
     *            defined in the langs.json file
     */
    private static void loadLang(final String userCustomLanguage)
    {
        final Map<String, String> languages = new Gson()
                .fromJson(new InputStreamReader(
                        OpenBST.class.getResourceAsStream(
                                "/utybo/branchingstorytree/swing/lang/langs.json"),
                        StandardCharsets.UTF_8), new TypeToken<Map<String, String>>()
                        {}.getType());
        try
        {
            Lang.loadTranslationsFromFile(Lang.getDefaultLanguage(),
                    OpenBST.class.getResourceAsStream(
                            "/utybo/branchingstorytree/swing/lang/" + languages.get("default")));
        }
        catch(final Exception e)
        {
            LOG.warn("Exception while loading language file : " + languages.get("default"), e);
        }
        if(userCustomLanguage != null)
        {
            Lang.setSelectedLanguage(new Locale(userCustomLanguage));
        }
        final Locale userLanguage = Lang.getSelectedLanguage();
        languages.forEach((k, v) ->
        {
            if(userLanguage.equals(new Locale(k)) && !v.equals(languages.get("default")))
            {
                try
                {
                    Lang.loadTranslationsFromFile(userLanguage, OpenBST.class
                            .getResourceAsStream("/utybo/branchingstorytree/swing/lang/" + v));
                }
                catch(final Exception e)
                {
                    LOG.warn("Exception while loading language file : " + v, e);
                }
            }
        });
    }

    public static String getAllLogs()
    {
        return logOutput.toString(Charset.defaultCharset());
    }
}
