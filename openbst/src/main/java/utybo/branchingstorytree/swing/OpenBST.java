/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.Color;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingWorker;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import utybo.branchingstorytree.swing.ext.ComparableVersion;
import utybo.branchingstorytree.swing.impl.IMGClient;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.OutputStreamToOutputAndPrint;
import utybo.branchingstorytree.swing.visuals.JBannerPanel;
import utybo.branchingstorytree.swing.visuals.Splashscreen;

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
        // Before we do anything, setup system properties
        // First one to ensure Java 9's scaling system gets out of the way
        System.setProperty("sun.java2d.uiScale", "1.0");
        // Second one to force hardware acceleration
        System.setProperty("sun.java2d.opengl", "true");

        LOG.info("OpenBST version " + VERSION + ", part of the BST project");
        LOG.trace("[ INIT ]");
        LOG.trace("Loading language files");
        loadLang(args.length > 0 ? args[0] : null);

        LOG.trace("Applying Look and Feel");
        OpenBSTGUI.initializeLaF();
        VisualsUtils.fixTextFontScaling();

        LOG.trace("Loading scaling factor");
        Icons.loadScalingFactor();

        Splashscreen sc = Splashscreen.start();
        SwingWorker<Void, String> sw = new SwingWorker<Void, String>()
        {

            @Override
            protected Void doInBackground()
            {
                LOG.trace("Initializing JavaFX");
                publish(Lang.get("splash.init"));
                // Necessary - because we are killing Scenes all the time with WebViews in NodePanels,
                // JFX may think we just ended our application.
                // OpenBST exits with a dirty System.exit() anyway.
                Platform.setImplicitExit(false);
                // Initialize JavaFX
                new JFXPanel();
                VisualsUtils.invokeJfxAndWait(() ->
                {
                    // Initialize the web engine
                    new WebEngine();
                    // Initialize a view
                    new WebView();
                });

                LOG.info("Loading icons...");
                publish(Lang.get("splash.icons"));
                long timeAtIconStart = System.currentTimeMillis();
                Icons.load();
                LOG.info("Time taken to load icons : "
                        + (System.currentTimeMillis() - timeAtIconStart) + " ms");

                LOG.info("Loading backgrounds...");
                publish(Lang.get("splash.loadbg"));
                Icons.loadBackgrounds();

                // $EXPERIMENTAL
                LOG.info("Caching backgrounds...");
                publish(Lang.get("splash.processbg"));
                IMGClient.initInternal();

                return null;
            }

            @Override
            protected void process(List<String> chunks)
            {
                String s = chunks.get(chunks.size() - 1);
                sc.setText(s);
            }

        };

        sw.execute();
        try
        {
            sw.get();
        }
        catch(InterruptedException | ExecutionException e1)
        {
            OpenBST.LOG.error(e1);
        }

        VisualsUtils.invokeSwingAndWait(() ->
        {
            sc.setText(Lang.get("splash.launch"));
            sc.stop();
        });
        LOG.trace("Launching app...");
        OpenBSTGUI.launch();

        VisualsUtils.invokeSwingAndWait(() -> sc.dispose());

        LOG.trace("Checking versions...");
        if(!"<unknown version>".equals(VERSION))
        {
            SwingWorker<UpdateInfo, Void> worker = new SwingWorker<UpdateInfo, Void>()
            {

                @Override
                protected UpdateInfo doInBackground() throws Exception
                {
                    URL updateInfoSite = new URL("https://utybo.github.io/BST/version.json");
                    UpdateInfo info = new Gson().fromJson(new InputStreamReader(
                            updateInfoSite.openStream(), StandardCharsets.UTF_8), UpdateInfo.class);
                    return info;
                }

                @Override
                protected void done()
                {
                    try
                    {
                        UpdateInfo remoteVersion = this.get();
                        ComparableVersion remoteUnstable = new ComparableVersion(
                                remoteVersion.unstable),
                                remoteStable = new ComparableVersion(remoteVersion.stable);
                        ComparableVersion local = new ComparableVersion(
                                VERSION.substring(0, VERSION.length() - 1));

                        if(VERSION.endsWith("u"))
                        {
                            // Local version is unstable
                            // Show updates to either the most recent unstable or the most recent stable
                            if(local.compareTo(remoteStable) < 0
                                    && remoteStable.compareTo(remoteUnstable) < 0)
                            {
                                // local (unstable) < stable < unstable
                                // Give options for both unstable and stable
                                JButton stablebtn = new JButton(Lang.get("up.moreinfostable"));
                                stablebtn.addActionListener(e ->
                                {
                                    VisualsUtils.browse(remoteVersion.stableurl);
                                });
                                JButton unstablebtn = new JButton(Lang.get("up.moreinfounstable"));
                                unstablebtn.addActionListener(e ->
                                {
                                    VisualsUtils.browse(remoteVersion.unstableurl);
                                });
                                OpenBSTGUI.getInstance()
                                        .addBanner(new JBannerPanel(
                                                new ImageIcon(
                                                        Icons.getImage("Installing Updates", 48)),
                                                new Color(142, 255, 159), Lang.get("up.message1"),
                                                stablebtn, false, unstablebtn));
                            }
                            else if(remoteStable.compareTo(local) < 0
                                    && local.compareTo(remoteUnstable) < 0)
                            {
                                // stable < local (unstable) < unstable
                                JButton unstablebtn = new JButton(Lang.get("up.moreinfo"));
                                unstablebtn.addActionListener(e ->
                                {
                                    VisualsUtils.browse(remoteVersion.unstableurl);
                                });
                                OpenBSTGUI.getInstance()
                                        .addBanner(new JBannerPanel(
                                                new ImageIcon(
                                                        Icons.getImage("Installing Updates", 48)),
                                                new Color(142, 255, 159), Lang.get("up.message2"),
                                                unstablebtn, false));
                            }
                            else if(remoteUnstable.compareTo(remoteStable) < 0
                                    && local.compareTo(remoteStable) < 0)
                            {
                                // local (unstable) < stable
                                // and unstable < stable
                                JButton stablebtn = new JButton(Lang.get("up.moreinfo"));
                                stablebtn.addActionListener(e ->
                                {
                                    VisualsUtils.browse(remoteVersion.stableurl);
                                });
                                OpenBSTGUI.getInstance()
                                        .addBanner(new JBannerPanel(
                                                new ImageIcon(
                                                        Icons.getImage("Installing Updates", 48)),
                                                new Color(142, 255, 159), Lang.get("up.message3"),
                                                stablebtn, false));
                            }
                        }
                        else
                        {
                            // If we're not running an unstable version, the only interesting case is local < stable
                            if(local.compareTo(remoteStable) < 0)
                            {
                                // local (stable) < stable
                                JButton stablebtn = new JButton(Lang.get("up.moreinfo"));
                                stablebtn.addActionListener(e ->
                                {
                                    VisualsUtils.browse(remoteVersion.stableurl);
                                });
                                OpenBSTGUI.getInstance()
                                        .addBanner(new JBannerPanel(
                                                new ImageIcon(
                                                        Icons.getImage("Installing Updates", 48)),
                                                new Color(142, 255, 159), Lang.get("up.message4"),
                                                stablebtn, false));
                            }
                        }
                    }

                    catch(InterruptedException | ExecutionException e)
                    {
                        LOG.warn("Failed to read update information", e);
                        JButton showDetails = new JButton(Lang.get("up.showdetails"));
                        showDetails.addActionListener(ev -> Messagers.showException(
                                OpenBSTGUI.getInstance(), Lang.get("up.failedmessage"), e));
                        OpenBSTGUI.getInstance()
                                .addBanner(new JBannerPanel(
                                        new ImageIcon(Icons.getImage("Cancel", 16)),
                                        new Color(255, 144, 144), Lang.get("up.failedbanner"),
                                        showDetails, false));
                    }
                }
            };
            worker.execute();
        }
    }

    public static class UpdateInfo
    {
        private String stable, stableurl, unstable, unstableurl;
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
