/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.overlay.SubstanceOverlayPainter;
import org.pushingpixels.substance.api.skin.BusinessSkin;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGoldLookAndFeel;
import org.pushingpixels.trident.Timeline;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.impl.BRMFileClient;
import utybo.branchingstorytree.swing.impl.TabClient;
import utybo.branchingstorytree.swing.utils.BSTPackager;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.Lang.UnrespectedModelException;
import utybo.branchingstorytree.swing.visuals.AboutDialog;
import utybo.branchingstorytree.swing.visuals.JBackgroundPanel;
import utybo.branchingstorytree.swing.visuals.JBannerPanel;
import utybo.branchingstorytree.swing.visuals.PackageDialog;
import utybo.branchingstorytree.swing.visuals.StoryPanel;

/**
 * OpenBST is an open source implementation of the BST language that aims to be
 * fully compatible with every single feature of BST.
 * <p>
 * This class is both the main class and the main JFrame.
 *
 * @author utybo
 *
 */
public class OpenBST extends JFrame
{
    /**
     * Version number of OpenBST
     */
    public static final String version;
    static
    {
        String s = OpenBST.class.getPackage().getImplementationVersion();
        if(s == null)
        {
            version = "<unknown version>";
        }
        else
        {
            version = s;
        }
    }
    private static final long serialVersionUID = 1L;

    public static final Logger LOG = LogManager.getLogger("OpenBST");

    /**
     * The parser that will be reused throughout the entire session.
     */
    private final BranchingStoryTreeParser parser = new BranchingStoryTreeParser();

    /**
     * The JFrame instance
     */
    private static OpenBST instance;

    private static final Random RANDOM = new Random();

    public static final Color OPENBST_BLUE = new Color(33, 150, 243);

    public static final SubstanceLookAndFeel DARK_THEME = new SubstanceGraphiteGoldLookAndFeel();
    public static final SubstanceLookAndFeel LIGHT_THEME;
    public static final LookAndFeel DEBUG_THEME = new MetalLookAndFeel();

    static
    {
        SubstanceSkin skin = new BusinessSkin();
        for(SubstanceOverlayPainter op : new ArrayList<>(
                skin.getOverlayPainters(DecorationAreaType.TOOLBAR)))
        {
            skin.removeOverlayPainter(op, DecorationAreaType.TOOLBAR);
        }
        LIGHT_THEME = new SubstanceLookAndFeel(skin)
        {
            private static final long serialVersionUID = 1L;
        };
    }

    // --- IMAGES ---

    public final static List<BufferedImage> bgImages = Collections
            .unmodifiableList(Arrays.asList(loadImages("images/bg$.jpg", 8)));

    /**
     * Container for all the tabs
     */
    private final JTabbedPane container;

    private final JBackgroundPanel background;

    private int selectedTheme = 1;
    private boolean dark = false;
    private static final Color DISCORD_COLOR = new Color(114, 137, 218);
    private final LinkedList<Consumer<Boolean>> darkModeCallbacks = new LinkedList<>();

    /**
     * Launch OpenBST
     *
     * @param args
     *            Arguments. The first argument is the language code to be used
     */
    public static void main(final String[] args)
    {
        LOG.info("OpenBST version " + version + ", part of the BST project");
        LOG.trace("[ INIT ]");

        LOG.trace("Loading language files");
        loadLang(args.length > 0 ? args[0] : null);

        LOG.trace("Initializing JavaFX");
        new JFXPanel();
        Platform.setImplicitExit(false);
        // Necessary - because we are killing Scenes all the time with WebViews in NodePanels,
        // JFX may think we just ended our application.
        // OpenBST exits with a dirty System.exit() anyway.
        LOG.trace("Applying Look and Feel");
        invokeSwingAndWait(() ->
        {
            try
            {
                UIManager.setLookAndFeel(LIGHT_THEME);
                UIManager.getDefaults().put(SubstanceLookAndFeel.COLORIZATION_FACTOR,
                        new Double(1.0D));

                if(System.getProperty("os.name").toLowerCase().equals("linux"))
                {
                    // Try to apply GNOME Shell fix
                    try
                    {
                        final Toolkit xToolkit = Toolkit.getDefaultToolkit();
                        final java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass()
                                .getDeclaredField("awtAppClassName");
                        awtAppClassNameField.setAccessible(true);
                        awtAppClassNameField.set(xToolkit, Lang.get("title"));
                        awtAppClassNameField.setAccessible(false);
                    }
                    catch(final Exception e)
                    {
                        LOG.warn("Could not apply X fix", e);
                    }
                }

            }
            catch(final Exception e)
            {
                LOG.warn("Could not apply Substance LaF, falling back to system LaF", e);
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch(final Exception e1)
                {
                    LOG.warn(
                            "Failed to load System LaF as well, falling back to keeping the default LaF",
                            e1);
                }
            }

            LOG.info("Loading icons...");
            Icons.load();

            new OpenBST();
        });

    }

    private static BufferedImage[] loadImages(String string, int length)
    {
        BufferedImage[] array = new BufferedImage[length];
        for(int i = 0; i < length; i++)
        {
            array[i] = loadXZImage(string.replace("$", "" + i));
        }
        return array;
    }

    private static void invokeSwingAndWait(Runnable r)
    {
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch(InvocationTargetException | InterruptedException e)
        {
            LOG.warn("Swing invocation failed", e);
        }
    }

    /**
     * Open a prompt asking for a BST File
     *
     * @return The file selected, or null if none was chosen/the dialog was
     *         closed
     */
    private File askForFile()
    {
        final FileDialog jfc = new FileDialog(instance);
        jfc.setTitle(Lang.get("file.title"));
        jfc.setLocationRelativeTo(instance);
        jfc.setVisible(true);
        if(jfc.getFile() != null)
        {
            LOG.trace("File selected");
            final File file = new File(jfc.getDirectory() + jfc.getFile());
            LOG.debug("[ LAUNCHING ]");
            return file;
        }
        else
        {
            LOG.trace("No file selected.");
            return null;
        }
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
    public void loadFile(final File file, final TabClient client, Consumer<BranchingStory> callback)
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
                        bs = BSTPackager.fromPackage(new ProgressMonitorInputStream(instance,
                                "Opening " + file.getName() + "...", new FileInputStream(file)),
                                client);
                    }
                    else
                    {
                        bs = parser.parse(
                                new BufferedReader(new InputStreamReader(
                                        new ProgressMonitorInputStream(instance,
                                                "Opening " + file.getName() + "...",
                                                new FileInputStream(file)),
                                        Charset.forName("UTF-8"))),
                                new Dictionary(), client, "<main>");
                        client.setBRMHandler(new BRMFileClient(file, client, bs));
                    }
                    callback.accept(bs);
                    return bs;
                }
                catch(final IOException e)
                {
                    LOG.error("IOException caught", e);
                    showMessageDialog(instance,
                            Lang.get("file.error").replace("$e", e.getClass().getSimpleName())
                                    .replace("$m", e.getMessage()),
                            Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                catch(final BSTException e)
                {
                    LOG.error("BSTException caught", e);
                    String s = Lang.get("file.bsterror.1");
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
                    if(doAndReturn(() -> JOptionPane.showConfirmDialog(instance, s2,
                            Lang.get("bsterror"), JOptionPane.ERROR_MESSAGE,
                            JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION)
                    {
                        LOG.debug("Reloading");
                        return doInBackground();
                    }
                    return null;
                }
                catch(final Exception e)
                {
                    LOG.error("Random exception caught", e);
                    showMessageDialog(instance, Lang.get("file.crash"), Lang.get("error"),
                            JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(instance, Lang.get("file.crash"),
                            Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Load the default language (which should be English) as well as the user's
     * language.. We avoid loading all the langauge files to avoid having our
     * RAM usage blowing up
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

    private static BufferedImage loadXZImage(String path)
    {
        try
        {
            return ImageIO.read(new XZCompressorInputStream(OpenBST.class
                    .getResourceAsStream("/utybo/branchingstorytree/swing/" + path + ".xz")));
        }
        catch(Exception e)
        {
            LOG.warn("Failed to load image at path " + path, e);
            return null;
        }
    }

    /**
     * Add a story by creating a tab and initializing its panel. Also triggers
     * post-creation events (such as NSFW warnings)
     *
     * @param story
     *            The story to create a tab for
     * @param file
     *            The file the story was loaded from
     * @param client
     *            The client to use
     * @return
     */
    private StoryPanel addStory(final BranchingStory story, final File file, final TabClient client)
    {
        LOG.trace("Creating tab");
        final StoryPanel sp = new StoryPanel(story, this, file, client);
        container.addTab(sp.getTitle(), null, sp, null);
        container.setSelectedIndex(container.getTabCount() - 1);
        if(!sp.postCreation())
        {
            container.removeTabAt(container.getTabCount() - 1);
            return null;
        }
        else
        {
            return sp;
        }
    }

    /**
     * Load all the icons and initialize the frame
     */
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public OpenBST()
    {
        instance = this;
        UIManager.put("OptionPane.errorIcon", new ImageIcon(Icons.getImage("Cancel", 48)));
        UIManager.put("OptionPane.informationIcon", new ImageIcon(Icons.getImage("About", 48)));
        UIManager.put("OptionPane.questionIcon", new ImageIcon(Icons.getImage("Rename", 48)));
        UIManager.put("OptionPane.warningIcon", new ImageIcon(Icons.getImage("Error", 48)));

        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(4);
        getContentPane().setLayout(borderLayout);
        setIconImage(Icons.getImage("Logo", 48));
        setTitle("OpenBST " + version);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel openBST = new JLabel(Lang.get("banner.titleextended"));
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER));

        Timeline tl = new Timeline(banner);
        tl.setDuration(200L);
        tl.addPropertyToInterpolate("background", OPENBST_BLUE, new Color(145, 145, 145));
        Timeline darkTl = new Timeline(banner);
        darkTl.setDuration(200L);
        darkTl.addPropertyToInterpolate("background", OPENBST_BLUE.darker().darker(),
                new Color(100, 100, 100));

        banner.setBackground(OPENBST_BLUE);
        banner.add(new JLabel(new ImageIcon(Icons.getImage("LogoWhite", 16))));
        openBST.setForeground(Color.WHITE);
        addDarkModeCallback(b ->
        {
            banner.setBackground(b ? OPENBST_BLUE.darker().darker() : OPENBST_BLUE);
        });
        banner.add(openBST);
        getContentPane().add(banner, BorderLayout.NORTH);

        banner.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                openBST.setText(Lang.get("banner.title"));
                createShortMenu().show(OpenBST.this, e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                if(dark)
                {
                    darkTl.play();
                }
                else
                {
                    tl.play();
                }
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                if(dark)
                {
                    darkTl.playReverse();
                }
                else
                {
                    tl.playReverse();
                }
            }

        });

        container = new JTabbedPane();
        getContentPane().add(container, BorderLayout.CENTER);

        final JBackgroundPanel welcomeContentPanel = new JBackgroundPanel(
                bgImages.get(RANDOM.nextInt(bgImages.size())), Image.SCALE_FAST);
        background = welcomeContentPanel;

        welcomeContentPanel.setLayout(new MigLayout("hidemode 2", "[grow,center]", "[][grow][]"));
        container.add(welcomeContentPanel);
        container.setTitleAt(0, Lang.get("welcome"));

        JPanel bannersPanel = new JPanel(new MigLayout("hidemode 2, gap 0px, fill, wrap 1, ins 0"));
        bannersPanel.setBackground(new Color(0, 0, 0, 0));
        welcomeContentPanel.add(bannersPanel, "cell 0 0,grow");

        if(version.endsWith("u"))
        {
            JButton btnReportBugs = new JButton(Lang.get("welcome.reportbugs"));
            btnReportBugs.addActionListener(e ->
            {
                try
                {
                    Desktop.getDesktop()
                            .browse(new URL("https://github.com/utybo/BST/issues").toURI());
                }
                catch(Exception e1)
                {
                    LOG.error("Exception during link opening", e1);
                }
            });
            bannersPanel.add(new JBannerPanel(new ImageIcon(Icons.getImage("Experiment", 32)),
                    Color.YELLOW, Lang.get("welcome.ontheedge"), btnReportBugs, false), "grow");
        }

        if(System.getProperty("java.specification.version").equals("9"))
        {
            bannersPanel.add(
                    new JBannerPanel(new ImageIcon(Icons.getImage("Attention", 32)),
                            new Color(255, 50, 50), Lang.get("welcome.java9warning"), null, true),
                    "grow");
        }

        JButton btnJoinDiscord = new JButton(Lang.get("openbst.discordjoin"));
        btnJoinDiscord.addActionListener(e ->
        {
            try
            {
                Desktop.getDesktop().browse(new URL("https://discord.gg/6SVDCMM").toURI());
            }
            catch(Exception e1)
            {
                LOG.error("Exception during link opening", e1);
            }
        });
        bannersPanel.add(new JBannerPanel(new ImageIcon(Icons.getImage("Discord", 48)),
                DISCORD_COLOR, Lang.get("openbst.discord"), btnJoinDiscord, false), "grow");

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 0));
        welcomeContentPanel.add(panel, "flowx,cell 0 1,growx,aligny center");
        panel.setLayout(new MigLayout("", "[40%][][][][60%,growprio 50]", "[][grow]"));

        final JLabel lblOpenbst = new JLabel(new ImageIcon(Icons.getImage("FullLogo", 48)));
        addDarkModeCallback(b -> lblOpenbst.setIcon(new ImageIcon(
                b ? Icons.getImage("FullLogoWhite", 48) : Icons.getImage("FullLogo", 48))));
        panel.add(lblOpenbst, "flowx,cell 0 0 1 2,alignx trailing,aligny center");

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        panel.add(separator, "cell 2 0 1 2,growy");

        final JLabel lblWelcomeToOpenbst = new JLabel("<html>" + Lang.get("welcome.intro"));
        lblWelcomeToOpenbst.setMaximumSize(new Dimension(350, 999999));
        panel.add(lblWelcomeToOpenbst, "cell 4 0");

        Component horizontalStrut = Box.createHorizontalStrut(10);
        panel.add(horizontalStrut, "cell 1 1");

        Component horizontalStrut_1 = Box.createHorizontalStrut(10);
        panel.add(horizontalStrut_1, "cell 3 1");

        final JButton btnOpenAFile = new JButton(Lang.get("welcome.open"));
        panel.add(btnOpenAFile, "cell 4 1");
        btnOpenAFile.setIcon(new ImageIcon(Icons.getImage("Open", 40)));
        btnOpenAFile.addActionListener(e ->
        {
            clickOpenStory();
        });

        JButton btnChangeBackground = new JButton(Lang.get("welcome.changebackground"),
                new ImageIcon(Icons.getImage("Change Theme", 16)));
        btnChangeBackground.addActionListener(e ->
        {
            BufferedImage prev = background.getImage();
            BufferedImage next;
            do
            {
                next = bgImages.get(RANDOM.nextInt(bgImages.size()));
            }
            while(prev == next);
            background.setImage(next);
        });
        welcomeContentPanel.add(btnChangeBackground, "flowx,cell 0 2,alignx left");

        JButton btnWelcomepixabay = new JButton(Lang.get("welcome.pixabay"),
                new ImageIcon(Icons.getImage("External Link", 16)));
        btnWelcomepixabay.addActionListener(e ->
        {
            try
            {
                Desktop.getDesktop().browse(new URL("https://pixabay.com").toURI());
            }
            catch(IOException | URISyntaxException e1)
            {
                LOG.warn("Failed to browse to Pixabay website", e1);
            }
        });
        welcomeContentPanel.add(btnWelcomepixabay, "cell 0 2");

        JLabel creds = new JLabel(Lang.get("welcome.credits"));
        creds.setEnabled(false);
        welcomeContentPanel.add(creds, "cell 0 2, gapbefore 10px");

        setSize((int)(830 * Icons.getScale()), (int)(480 * Icons.getScale()));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Remove a story panel from the tabs
     *
     * @param storyPanel
     */
    public void removeStory(final StoryPanel storyPanel)
    {
        container.remove(storyPanel);
    }

    public void clickOpenStory()
    {
        final File f = askForFile();
        if(f != null)
        {
            final TabClient client = new TabClient(instance);
            loadFile(f, client, new Consumer<BranchingStory>()
            {
                private StoryPanel sp;

                @Override
                public void accept(BranchingStory bs)
                {
                    if(bs != null)
                    {
                        try
                        {
                            SwingUtilities.invokeAndWait(() -> sp = addStory(bs, f, client));
                            if(sp != null)
                            {
                                try
                                {
                                    client.getBRMHandler().load();
                                }
                                catch(BSTException e)
                                {
                                    LOG.error("Exception caught while loading resources", e);
                                    showMessageDialog(instance,
                                            Lang.get("file.resourceerror")
                                                    .replace("$e", whichCause(e))
                                                    .replace("$m", whichMessage(e)),
                                            Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                                }
                                SwingUtilities.invokeAndWait(() -> sp.setupStory());
                            }
                        }
                        catch(InvocationTargetException | InterruptedException e)
                        {
                            LOG.warn("Swing invocation exception", e);
                        }

                    }
                }

                private String whichMessage(BSTException e)
                {
                    if(e.getCause() != null)
                    {
                        return e.getCause().getMessage();
                    }
                    else
                    {
                        return e.getMessage();
                    }
                }

                private String whichCause(BSTException e)
                {
                    if(e.getCause() != null)
                    {
                        return e.getCause().getClass().getSimpleName();
                    }
                    else
                    {
                        return e.getClass().getSimpleName();
                    }
                }
            });
        }
    }

    public JPopupMenu createShortMenu()
    {
        JPopupMenu shortMenu = new JPopupMenu();
        JMenuItem label = new JMenuItem(Lang.get("menu.title"));
        label.setEnabled(false);
        shortMenu.add(label);
        shortMenu.addSeparator();
        shortMenu.add(new JMenuItem(
                new AbstractAction(Lang.get("menu.open"), new ImageIcon(Icons.getImage("Open", 16))) // TODO
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        clickOpenStory();
                    }
                }));

        shortMenu.addSeparator();

        JMenu additionalMenu = new JMenu(Lang.get("menu.advanced"));
        shortMenu.add(additionalMenu);

        additionalMenu.add(new JMenuItem(new AbstractAction(Lang.get("menu.package"),
                new ImageIcon(Icons.getImage("Open Archive", 16)))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                new PackageDialog(instance).setVisible(true);
            }
        }));
        additionalMenu.add(new JMenuItem(new AbstractAction(Lang.get("langcheck"))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                final Map<String, String> languages = new Gson()
                        .fromJson(
                                new InputStreamReader(
                                        OpenBST.class.getResourceAsStream(
                                                "/utybo/branchingstorytree/swing/lang/langs.json"),
                                        StandardCharsets.UTF_8),
                                new TypeToken<Map<String, String>>()
                                {}.getType());
                languages.remove("en");
                languages.remove("default");
                JComboBox<String> jcb = new JComboBox<>(new Vector<>(languages.keySet()));
                JPanel panel = new JPanel();
                panel.add(new JLabel(Lang.get("langcheck.choose")));
                panel.add(jcb);
                int result = JOptionPane.showOptionDialog(OpenBST.this, panel,
                        Lang.get("langcheck"), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if(result == JOptionPane.OK_OPTION)
                {
                    Locale selected = new Locale((String)jcb.getSelectedItem());
                    if(!Lang.getMap().keySet().contains(selected))
                    {
                        try
                        {
                            Lang.loadTranslationsFromFile(selected, OpenBST.class
                                    .getResourceAsStream("/utybo/branchingstorytree/swing/lang/"
                                            + languages.get(jcb.getSelectedItem().toString())));
                        }
                        catch(UnrespectedModelException | IOException e1)
                        {
                            LOG.warn("Failed to load translation file", e1);
                        }
                    }
                    ArrayList<String> list = new ArrayList<>();
                    Lang.getLocaleMap(Locale.ENGLISH).forEach((k, v) ->
                    {
                        if(!Lang.getLocaleMap(selected).containsKey(k))
                        {
                            list.add(k + "\n");
                        }
                    });
                    StringBuilder sb = new StringBuilder();
                    Collections.sort(list);
                    list.forEach(s -> sb.append(s));
                    JDialog dialog = new JDialog(OpenBST.this, Lang.get("langcheck"));
                    dialog.getContentPane().setLayout(new MigLayout());
                    dialog.getContentPane().add(new JLabel(Lang.get("langcheck.result")),
                            "pushx, growx, wrap");
                    JTextArea area = new JTextArea();
                    area.setLineWrap(true);
                    area.setWrapStyleWord(true);
                    area.setText(sb.toString());
                    area.setEditable(false);
                    area.setBorder(BorderFactory.createLoweredBevelBorder());
                    JScrollPane jsp = new JScrollPane(area);
                    jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    dialog.getContentPane().add(jsp, "pushx, pushy, growx, growy");
                    dialog.setSize(300, 300);
                    dialog.setLocationRelativeTo(OpenBST.this);
                    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                    dialog.setVisible(true);
                }
            }
        }));

        shortMenu.addSeparator();

        JMenu themesMenu = new JMenu(Lang.get("menu.themes"));
        shortMenu.add(themesMenu);
        themesMenu.setIcon(new ImageIcon(Icons.getImage("Color Wheel", 16)));
        ButtonGroup themesGroup = new ButtonGroup();
        JRadioButtonMenuItem jrbmi;

        jrbmi = new JRadioButtonMenuItem(Lang.get("menu.themes.dark"));
        if(0 == selectedTheme)
        {
            jrbmi.setSelected(true);
        }
        jrbmi.addActionListener(e -> switchLaF(0, DARK_THEME));
        themesMenu.add(jrbmi);
        themesGroup.add(jrbmi);

        jrbmi = new JRadioButtonMenuItem(Lang.get("menu.themes.light"));
        if(1 == selectedTheme)
        {
            jrbmi.setSelected(true);
        }
        jrbmi.addActionListener(e -> switchLaF(1, LIGHT_THEME));
        themesMenu.add(jrbmi);
        themesGroup.add(jrbmi);

        jrbmi = new JRadioButtonMenuItem(Lang.get("menu.themes.debug"));
        if(2 == selectedTheme)
        {
            jrbmi.setSelected(true);
        }
        jrbmi.addActionListener(e -> switchLaF(2, DEBUG_THEME));
        themesMenu.add(jrbmi);
        themesGroup.add(jrbmi);

        shortMenu.add(new JMenuItem(new AbstractAction(Lang.get("menu.about"),
                new ImageIcon(Icons.getImage("About", 16)))
        {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                new AboutDialog(instance).setVisible(true);
            }
        }));

        return shortMenu;
    }

    private void switchLaF(int id, LookAndFeel laf)
    {

        try
        {
            dark = id == 0;
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(instance);
            background.setDark(id == 0);
            darkModeCallbacks.forEach(a -> a.accept(id == 0));
            selectedTheme = id;
        }
        catch(UnsupportedLookAndFeelException e)
        {
            LOG.warn("Unsupported LaF", e);
        }
    }

    public static OpenBST getInstance()
    {
        return instance;
    }

    protected void showMessageDialog(OpenBST obst, String msg, String head, int type)
    {
        invokeSwingAndWait(() -> JOptionPane.showMessageDialog(obst, msg, head, type));
    }

    public void addDarkModeCallback(Consumer<Boolean> callback)
    {
        darkModeCallbacks.add(callback);
    }

    public void removeDarkModeCallbback(Consumer<Boolean> callback)
    {
        darkModeCallbacks.remove(callback);
    }

    public Boolean isDark()
    {
        return dark;
    }
}
