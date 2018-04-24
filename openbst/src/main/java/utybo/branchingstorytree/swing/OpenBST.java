/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import static utybo.branchingstorytree.swing.VisualsUtils.invokeSwingAndWait;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.api.script.Dictionary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.ext.ComparableVersion;
import utybo.branchingstorytree.swing.impl.BRMFileClient;
import utybo.branchingstorytree.swing.impl.IMGClient;
import utybo.branchingstorytree.swing.impl.LoadStatusCallback;
import utybo.branchingstorytree.swing.impl.TabClient;
import utybo.branchingstorytree.swing.utils.AlphanumComparator;
import utybo.branchingstorytree.swing.utils.BSTPackager;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.OutputStreamToOutputAndPrint;
import utybo.branchingstorytree.swing.visuals.AccumulativeRunnable;
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

    private static Map<String, String> internalFiles;

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

    private static AbstractBSTGUI gui;
    private static final BranchingStoryTreeParser parser = new BranchingStoryTreeParser();

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

        InputStream inSplashImg = OpenBST.class.getResourceAsStream("/splashscreen.png");
        BufferedImage splashImg = null;
        if(inSplashImg != null)
            try
            {
                splashImg = ImageIO.read(inSplashImg);
            }
            catch(IOException e2)
            {
                LOG.error("Error on loading splashscreen.png", e2);
            }

        Splashscreen sc = Splashscreen.start(splashImg);
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

                // $EXPERIMENTAL
                LOG.info("Loading internal BTS files...");
                publish(Lang.get("splash.loadinternalbst"));
                loadInternalBSTFiles();

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

        // First init pass succesful.

        // Now, either second init pass if we're in embedded mode, or regular launch otherwise

        InputStream is = OpenBST.class.getResourceAsStream("/embed.bsp");
        if(is != null)
        {
            // Load the embedded story
            try
            {
                AccumulativeRunnable<String> acrun = new AccumulativeRunnable<String>()
                {
                    @Override
                    public void run(List<String> retrieveObjects)
                    {
                        sc.setText(retrieveObjects.get(retrieveObjects.size() - 1));
                    }
                };
                File tmpFile = File.createTempFile("openbstembed", ".bsp");
                FileOutputStream fos = new FileOutputStream(tmpFile);

                acrun.add("Extracting embedded story...");
                IOUtils.copy(is, fos);

                acrun.add("Loading embedded story...");
                SinglePanelGUI[] spg = new SinglePanelGUI[1];
                VisualsUtils.invokeSwingAndWait(() ->
                {
                    spg[0] = new SinglePanelGUI();
                });
                gui = spg[0];
                TabClient tc = new TabClient(spg[0]);

                FileInputStream fis = new FileInputStream(tmpFile);
                BranchingStory bs = BSTPackager.fromPackage(fis, tc, sc::setText);

                acrun.add("Initializing elements...");
                VisualsUtils.invokeSwingAndWait(() -> spg[0].setStory(bs, tmpFile, tc));

                acrun.add("Loading resources...");
                tc.getBRMHandler().setLoadCallback(new LoadStatusCallback()
                {
                    int total;

                    @Override
                    public void updateStatus(int i, String message)
                    {
                        acrun.add(message + " (" + i + "/" + total + ")");
                    }

                    @Override
                    public void setTotal(int i)
                    {
                        total = i;
                    }

                    @Override
                    public void close()
                    {}
                });
                tc.getBRMHandler().load();
                SwingUtilities.invokeAndWait(() ->
                {
                    sc.setText(Lang.get("splash.launch"));
                    sc.lock();
                    sc.stop();
                    spg[0].setVisible(true);
                    spg[0].begin();
                    sc.dispose();
                });

            }
            catch(IOException ioe)
            {
                LOG.error("Failed to load embedded story", ioe);
                Messagers.showException(sc, "Failed to load embedded file", ioe);
            }
            catch(BSTException e)
            {
                LOG.error("Failed to load BST file", e);
                Messagers.showException(sc, "Failed to parse embedded file", e);
            }
            catch(Exception e)
            {
                LOG.error("Unexpected exception", e);
                Messagers.showException(sc, "Unexpected exception while loading the file", e);
            }

            return; // Do not continue
        }

        VisualsUtils.invokeSwingAndWait(() ->
        {
            sc.setText(Lang.get("splash.launch"));
            sc.lock();
            sc.stop();
        });
        LOG.trace("Launching app...");
        OpenBSTGUI frame = OpenBSTGUI.launch();
        gui = frame;

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
                                frame.addBanner(new JBannerPanel(
                                        new ImageIcon(Icons.getImage("Installing Updates", 48)),
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
                                frame.addBanner(new JBannerPanel(
                                        new ImageIcon(Icons.getImage("Installing Updates", 48)),
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
                                frame.addBanner(new JBannerPanel(
                                        new ImageIcon(Icons.getImage("Installing Updates", 48)),
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
                                frame.addBanner(new JBannerPanel(
                                        new ImageIcon(Icons.getImage("Installing Updates", 48)),
                                        new Color(142, 255, 159), Lang.get("up.message4"),
                                        stablebtn, false));
                            }
                        }
                    }

                    catch(InterruptedException | ExecutionException e)
                    {
                        LOG.warn("Failed to read update information", e);
                        JButton showDetails = new JButton(Lang.get("up.showdetails"));
                        showDetails.addActionListener(ev -> Messagers.showException(frame,
                                Lang.get("up.failedmessage"), e));
                        frame.addBanner(
                                new JBannerPanel(new ImageIcon(Icons.getImage("Cancel", 16)),
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

    public static AbstractBSTGUI getGUIInstance()
    {
        return gui;
    }

    public static String getAllLogs()
    {
        return logOutput.toString(Charset.defaultCharset());
    }

    @Experimental
    private static void loadInternalBSTFiles()
    {
        Type type = new TypeToken<Map<String, String>>()
        {}.getType();
        internalFiles = new TreeMap<String, String>(new AlphanumComparator());
        internalFiles.putAll(new Gson()
                .fromJson(new InputStreamReader(OpenBST.class.getResourceAsStream("/bst/list.json"),
                        StandardCharsets.UTF_8), type));

    }

    @Experimental
    public static Map<String, String> getInternalFiles()
    {
        return Collections.unmodifiableMap(internalFiles);
    }

    /**
     * Load and parse a file, using appropriate dialogs if an error occurs to
     * inform the user and even give him the option to reload the file
     *
     * @param file
     *            The file to load
     * @param client
     *            The BST Client. This is required for parsing the file
     * @return
     */
    public static void loadFile(final File file, final TabClient client,
            Consumer<BranchingStory> callback)
    {
        SwingWorker<BranchingStory, Object> worker = new SwingWorker<BranchingStory, Object>()
        {
            @Override
            protected BranchingStory doInBackground() throws Exception
            {
                try
                {
                    LOG.trace("Parsing story");
                    String ext = FilenameUtils.getExtension(file.getName());
                    BranchingStory bs = null;
                    if(ext.equals("bsp"))
                    {
                        ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
                                getGUIInstance(), "Opening " + file.getName() + "...",
                                new FileInputStream(file));
                        bs = BSTPackager.fromPackage(pmis, client,
                                pmis.getProgressMonitor()::setNote);
                    }
                    else
                    {
                        bs = parser.parse(
                                new BufferedReader(new InputStreamReader(
                                        new ProgressMonitorInputStream(getGUIInstance(),
                                                "Opening " + file.getName() + "...",
                                                new FileInputStream(file)),
                                        StandardCharsets.UTF_8)),
                                new Dictionary(), client, "<main>");
                        client.setBRMHandler(new BRMFileClient(file, client, bs));
                    }
                    callback.accept(bs);
                    return bs;
                }
                catch(final IOException e)
                {
                    LOG.error("IOException caught", e);
                    Messagers.showException(getGUIInstance(),
                            Lang.get("file.error").replace("$e", e.getClass().getSimpleName())
                                    .replace("$m", e.getMessage()),
                            e);
                    return null;
                }
                catch(final BSTException e)
                {
                    LOG.error("BSTException caught", e);
                    String s = "<html>" + Lang.get("file.bsterror.1");
                    s += Lang.get("file.bsterror.2");
                    s += Lang.get("file.bsterror.3").replace("$l", "" + e.getWhere()).replace("$f",
                            "[main]");
                    if(e.getCause() != null)
                    {
                        s += Lang.get("file.bsterror.4")
                                .replace("$e", e.getCause().getClass().getSimpleName())
                                .replace("$m", e.getCause().getMessage());
                    }
                    s += Lang.get("file.bsterror.5").replace("$m", "" + e.getMessage());
                    s += Lang.get("file.bsterror.6");
                    String s2 = s;
                    if(doAndReturn(() -> Messagers.showConfirm(getGUIInstance(), s2,
                            Messagers.OPTIONS_YES_NO, Messagers.TYPE_ERROR,
                            Lang.get("bsterror"))) == Messagers.OPTION_YES)
                    {
                        LOG.debug("Reloading");
                        return doInBackground();
                    }
                    return null;
                }
                catch(final Exception e)
                {
                    LOG.error("Random exception caught", e);
                    Messagers.showException(getGUIInstance(), Lang.get("file.crash"), e);
                    return null;
                }

            }

            private <T> T doAndReturn(Supplier<T> supplier)
            {
                ArrayList<T> l = new ArrayList<>();
                invokeSwingAndWait(() ->
                {
                    l.add(supplier.get());
                });
                return l.size() == 0 ? null : l.get(0);
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                }
                catch(InterruptedException e)
                {
                    // Shouldn't happen
                }
                catch(ExecutionException e)
                {
                    LOG.error("Random exception caught", e);
                    Messagers.showException(getGUIInstance(), Lang.get("file.crash"), e);
                }
            }
        };
        worker.execute();
    }

    private OpenBST()
    {}
}
