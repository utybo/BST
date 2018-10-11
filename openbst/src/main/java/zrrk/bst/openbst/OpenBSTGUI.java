/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst;

import static zrrk.bst.openbst.OpenBST.LOG;
import static zrrk.bst.openbst.VisualsUtils.invokeSwingAndWait;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.io.IOUtils;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.SubstanceSlices.DecorationAreaType;
import org.pushingpixels.substance.api.painter.overlay.SubstanceOverlayPainter;
import org.pushingpixels.substance.api.skin.BusinessSkin;
import org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteChalkLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGoldLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.miginfocom.swing.MigLayout;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.openbst.editor.StoryEditor;
import zrrk.bst.openbst.impl.TabClient;
import zrrk.bst.openbst.utils.Lang;
import zrrk.bst.openbst.utils.Lang.UnrespectedModelException;
import zrrk.bst.openbst.visuals.AboutDialog;
import zrrk.bst.openbst.visuals.EmbedDialog;
import zrrk.bst.openbst.visuals.JBackgroundPanel;
import zrrk.bst.openbst.visuals.JBannerPanel;
import zrrk.bst.openbst.visuals.PackageDialog;
import zrrk.bst.openbst.visuals.StoryPanel;

@SuppressWarnings("serial")
public class OpenBSTGUI extends AbstractBSTGUI
{
    private static OpenBSTGUI instance;
    private static final Color DISCORD_COLOR = new Color(114, 137, 218);
    public static final Color OPENBST_BLUE = new Color(33, 150, 243);

    public static final SubstanceLookAndFeel DARK_THEME = new SubstanceGraphiteGoldLookAndFeel();
    public static final SubstanceLookAndFeel LIGHT_THEME;
    public static final LookAndFeel DEBUG_THEME = new MetalLookAndFeel();

    public static final Map<String, LookAndFeel> ADDITIONAL_LIGHT_THEMES, ADDITIONAL_DARK_THEMES;
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

        TreeMap<String, LookAndFeel> mapl = new TreeMap<>();
        TreeMap<String, LookAndFeel> mapd = new TreeMap<>();
        mapl.put("Autumn", new SubstanceAutumnLookAndFeel());
        mapl.put("Business Black Steel", new SubstanceBusinessBlackSteelLookAndFeel());
        mapl.put("Business Blue Steel", new SubstanceBusinessBlueSteelLookAndFeel());
        mapl.put("Cerulean", new SubstanceCeruleanLookAndFeel());
        mapl.put("Creme Coffee", new SubstanceCremeCoffeeLookAndFeel());
        mapl.put("Creme", new SubstanceCremeLookAndFeel());
        mapl.put("Dust Coffee", new SubstanceDustCoffeeLookAndFeel());
        mapl.put("Dust", new SubstanceDustLookAndFeel());
        mapl.put("Gemini", new SubstanceGeminiLookAndFeel());
        mapd.put("Graphite Aqua", new SubstanceGraphiteAquaLookAndFeel());
        mapd.put("Graphite Chalk", new SubstanceGraphiteChalkLookAndFeel());
        mapd.put("Graphite Glass", new SubstanceGraphiteGlassLookAndFeel());
        mapd.put("Graphite", new SubstanceGraphiteLookAndFeel());
        mapd.put("Magellan", new SubstanceMagellanLookAndFeel());
        mapl.put("Mariner", new SubstanceMarinerLookAndFeel());
        mapl.put("Moderate", new SubstanceModerateLookAndFeel());
        mapl.put("Nebula Brick Wall", new SubstanceNebulaBrickWallLookAndFeel());
        mapl.put("Nebula", new SubstanceNebulaLookAndFeel());
        mapl.put("Office 2007 (Black)", new SubstanceOfficeBlack2007LookAndFeel());
        mapl.put("Office 2007 (Blue)", new SubstanceOfficeBlue2007LookAndFeel());
        mapl.put("Office 2007 (Silver)", new SubstanceOfficeSilver2007LookAndFeel());
        mapd.put("Raven", new SubstanceRavenLookAndFeel());
        mapl.put("Sahara", new SubstanceSaharaLookAndFeel());
        mapd.put("Twilight", new SubstanceTwilightLookAndFeel());
        ADDITIONAL_LIGHT_THEMES = Collections.unmodifiableMap(mapl);
        ADDITIONAL_DARK_THEMES = Collections.unmodifiableMap(mapd);
    }

    /**
     * Container for all the tabs
     */
    private final JTabbedPane container;

    private final JBackgroundPanel background;

    private final JPanel bannersPanel;

    private JMenu shortMenu;

    private static int selectedTheme = 1;
    private static boolean dark = false;
    private static final LinkedList<Consumer<Boolean>> darkModeCallbacks = new LinkedList<>();

    public static OpenBSTGUI launch()
    {
        VisualsUtils.invokeSwingAndWait(() ->
        {
            instance = new OpenBSTGUI();
            instance.setVisible(true);
        });
        return instance;
    }

    protected static void initializeLaF()
    {
        invokeSwingAndWait(() ->
        {
            try
            {
                UIManager.setLookAndFeel(LIGHT_THEME);
                SubstanceCortex.GlobalScope.setColorizationFactor(1.0D);

                if(System.getProperty("os.name").toLowerCase().equals("linux"))
                {
                    // Try to apply GNOME Shell fix
                    try
                    {
                        final Toolkit xToolkit = Toolkit.getDefaultToolkit();
                        java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass()
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
        });
    }

    ///////// FRAME CREATION AND COMPONENTS

    public OpenBSTGUI()
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
        setTitle("OpenBST " + OpenBST.VERSION);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                boolean cancelled = false;
                int i = 0;
                for(Component c : container.getComponents())
                {
                    if(c instanceof StoryPanel)
                    {
                        i++;
                    }
                    else if(c instanceof StoryEditor)
                    {
                        container.setSelectedComponent(c);
                        if(((StoryEditor)c).askClose())
                        {
                            continue;
                        }
                        else
                        {
                            cancelled = true;
                            break;
                        }
                    }
                }
                if(!cancelled)
                {
                    if(i > 0)
                    {
                        int j = Messagers.showConfirm(OpenBSTGUI.this,
                                "You are about to close " + i
                                        + " file(s). Are you sure you wish to exit OpenBST?",
                                Messagers.OPTIONS_YES_NO, Messagers.TYPE_WARNING,
                                "Closing OpenBST");
                        if(j != Messagers.OPTION_YES)
                            cancelled = true;
                    }
                    if(!cancelled)
                        System.exit(0);
                }
            }

        });

        JMenuBar jmb = new JMenuBar();
        jmb.setBackground(OPENBST_BLUE);
        jmb.add(Box.createHorizontalGlue());
        jmb.add(createShortMenu());
        jmb.add(Box.createHorizontalGlue());
        this.setJMenuBar(jmb);

        addDarkModeCallback(b ->
        {
            jmb.setBackground(b ? OPENBST_BLUE.darker().darker() : OPENBST_BLUE);
        });

        container = new JTabbedPane();
        container.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        container.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                if(SwingUtilities.isMiddleMouseButton(e))
                {
                    final int i = container.indexAtLocation(e.getX(), e.getY());
                    if(i > -1)
                    {
                        askCloseTab(i);
                    }
                }
            }
        });
        getContentPane().add(container, BorderLayout.CENTER);

        final JBackgroundPanel welcomeContentPanel = new JBackgroundPanel(
                Icons.getRandomBackground(), Image.SCALE_FAST);
        background = welcomeContentPanel;

        welcomeContentPanel.setLayout(new MigLayout("hidemode 2", "[grow,center]", "[][grow][]"));
        container.add(welcomeContentPanel);
        container.setTitleAt(0, Lang.get("welcome"));

        bannersPanel = new JPanel(new MigLayout("hidemode 2, gap 0px, fill, wrap 1, ins 0"));
        bannersPanel.setBackground(new Color(0, 0, 0, 0));
        welcomeContentPanel.add(bannersPanel, "cell 0 0,grow");

        if(OpenBST.VERSION.endsWith("u"))
        {
            JButton btnReportBugs = new JButton(Lang.get("welcome.reportbugs"));
            btnReportBugs.addActionListener(e ->
            {
                VisualsUtils.browse("https://github.com/utybo/BST/issues");
            });
            bannersPanel.add(new JBannerPanel(new ImageIcon(Icons.getImage("Experiment", 32)),
                    Color.YELLOW, Lang.get("welcome.ontheedge"), btnReportBugs, false), "grow");
        }
        else if(OpenBST.VERSION.contains("SNAPSHOT"))
        {
            bannersPanel.add(new JBannerPanel(new ImageIcon(Icons.getImage("Experiment", 32)),
                    Color.ORANGE, Lang.get("welcome.snapshot"), null, false), "grow");
        }

        JButton btnJoinDiscord = new JButton(Lang.get("openbst.discordjoin"));
        btnJoinDiscord.addActionListener(e ->
        {
            VisualsUtils.browse("https://discord.gg/6SVDCMM");
        });
        bannersPanel.add(new JBannerPanel(new ImageIcon(Icons.getImage("Discord", 48)),
                DISCORD_COLOR, Lang.get("openbst.discord"), btnJoinDiscord, true), "grow");

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
        panel.add(btnOpenAFile, "flowx,cell 4 1");
        btnOpenAFile.setIcon(new ImageIcon(Icons.getImage("Open", 40)));
        btnOpenAFile.addActionListener(e ->
        {
            openStory(VisualsUtils.askForFile(this, Lang.get("file.title")));
        });

        final JButton btnOpenEditor = new JButton(Lang.get("welcome.openeditor"));
        panel.add(btnOpenEditor, "cell 4 1");
        btnOpenEditor.setIcon(new ImageIcon(Icons.getImage("Edit Property", 40)));
        btnOpenEditor.addActionListener(e ->
        {
            openEditor(VisualsUtils.askForFile(this, Lang.get("file.title")));
        });

        JButton btnChangeBackground = new JButton(Lang.get("welcome.changebackground"),
                new ImageIcon(Icons.getImage("Change Theme", 16)));
        btnChangeBackground.addActionListener(e ->
        {
            BufferedImage prev = background.getImage();
            BufferedImage next;
            do
            {
                next = Icons.getRandomBackground();
            }
            while(prev == next);
            background.setImage(next);
        });
        welcomeContentPanel.add(btnChangeBackground, "flowx,cell 0 2,alignx left");

        JButton btnWelcomepixabay = new JButton(Lang.get("welcome.pixabay"),
                new ImageIcon(Icons.getImage("External Link", 16)));
        btnWelcomepixabay.addActionListener(e ->
        {
            VisualsUtils.browse("https://pixabay.com");

        });
        welcomeContentPanel.add(btnWelcomepixabay, "cell 0 2");

        JLabel creds = new JLabel(Lang.get("welcome.credits"));
        creds.setEnabled(false);
        welcomeContentPanel.add(creds, "cell 0 2, gapbefore 10px");

        installKeyboardShortcuts();

        setSize((int)(830 * Icons.getScale()), (int)(480 * Icons.getScale()));
        setLocationRelativeTo(null);
    }

    private void askCloseTab(int i)
    {
        Component c = container.getComponentAt(i);
        if(c instanceof StoryPanel)
        {
            container.setSelectedComponent(c);
            ((StoryPanel)c).askClose();
        }
        else if(c instanceof StoryEditor)
        {
            container.setSelectedComponent(c);
            ((StoryEditor)c).askClose();
        }
    }

    private void installKeyboardShortcuts()
    {
        InputMap inputmap = getRootPane()
                .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionmap = getRootPane().getActionMap();

        // Commented out are the keystrokes that use the accelerator 
        // mechanism instead
        // inputmap.put(KeyStroke.getKeyStroke("control O"), "open");
        inputmap.put(KeyStroke.getKeyStroke("control shift O"), "openEditor");
        // inputmap.put(KeyStroke.getKeyStroke("control N"), "newEditor");
        inputmap.put(KeyStroke.getKeyStroke("control RIGHT"), "nextTab");
        inputmap.put(KeyStroke.getKeyStroke("control LEFT"), "previousTab");
        inputmap.put(KeyStroke.getKeyStroke("control W"), "closeTab");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "openMenu");

        actionmap.put("openMenu", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                shortMenu.doClick();
            }
        });

        actionmap.put("open", new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                openStory(VisualsUtils.askForFile(OpenBSTGUI.this, Lang.get("file.title")));
            }
        });

        actionmap.put("openEditor", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                openEditor(VisualsUtils.askForFile(OpenBSTGUI.this, Lang.get("file.title")));
            }
        });

        actionmap.put("newEditor", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewEditor();
            }
        });

        actionmap.put("nextTab", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                container.setSelectedIndex(
                        (container.getSelectedIndex() + 1) % container.getTabCount());
            }
        });

        actionmap.put("previousTab", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int toSelect = container.getSelectedIndex() - 1;
                if(toSelect < 0)
                    toSelect = container.getTabCount() - 1;
                container.setSelectedIndex(toSelect);
            }
        });

        actionmap.put("closeTab", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                askCloseTab(container.getSelectedIndex());
            }
        });
    }

    private JMenu createShortMenu()
    {
        shortMenu = new JMenu();
        addDarkModeCallback(b ->
        {
            shortMenu.setBackground(b ? OPENBST_BLUE.darker().darker() : OPENBST_BLUE.brighter());
            shortMenu.setForeground(b ? Color.WHITE : OPENBST_BLUE);
        });
        shortMenu.setBackground(OPENBST_BLUE.brighter());
        shortMenu.setForeground(OPENBST_BLUE);
        shortMenu.setText(Lang.get("banner.title"));
        shortMenu.setIcon(new ImageIcon(Icons.getImage("Logo", 16)));
        JMenuItem label = new JMenuItem(Lang.get("menu.title"));
        label.setEnabled(false);
        shortMenu.add(label);
        shortMenu.addSeparator();
        JMenuItem jmi = new JMenuItem(
                new AbstractAction(Lang.get("menu.open"), new ImageIcon(Icons.getImage("Open", 16)))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        openStory(VisualsUtils.askForFile(OpenBSTGUI.this, Lang.get("file.title")));
                    }
                });
        jmi.setAccelerator(KeyStroke.getKeyStroke("control O"));
        shortMenu.add(jmi);
        shortMenu.addSeparator();

        jmi = new JMenuItem(new AbstractAction(Lang.get("menu.create"),
                new ImageIcon(Icons.getImage("Add Property", 16)))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewEditor();
            }
        });
        jmi.setAccelerator(KeyStroke.getKeyStroke("control N"));
        shortMenu.add(jmi);

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

        additionalMenu.add(new JMenuItem(new AbstractAction("Create a runnable JAR file",
                new ImageIcon(Icons.getImage("Software Installer", 16)))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                new EmbedDialog().setVisible(true);
            }
        }));
        additionalMenu.add(new JMenuItem(new AbstractAction(Lang.get("langcheck"),
                new ImageIcon(Icons.getImage("LangCheck", 16)))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                final Map<String, String> languages = new Gson()
                        .fromJson(
                                new InputStreamReader(
                                        OpenBST.class.getResourceAsStream(
                                                "/zrrk/bst/openbstlang/langs.json"),
                                        StandardCharsets.UTF_8),
                                new TypeToken<Map<String, String>>()
                                {}.getType());
                languages.remove("en");
                languages.remove("default");
                JComboBox<String> jcb = new JComboBox<>(new Vector<>(languages.keySet()));
                JPanel panel = new JPanel();
                panel.add(new JLabel(Lang.get("langcheck.choose")));
                panel.add(jcb);
                int result = JOptionPane.showOptionDialog(OpenBSTGUI.this, panel,
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
                                    .getResourceAsStream("/zrrk/bst/openbstlang/"
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
                    JDialog dialog = new JDialog(OpenBSTGUI.this, Lang.get("langcheck"));
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
                    dialog.setSize((int)(Icons.getScale() * 300), (int)(Icons.getScale() * 300));
                    dialog.setLocationRelativeTo(OpenBSTGUI.this);
                    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                    dialog.setVisible(true);
                }
            }
        }));

        additionalMenu.add(new JMenuItem(new AbstractAction(Lang.get("menu.debug"),
                new ImageIcon(Icons.getImage("Code", 16)))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                DebugInfo.launch(OpenBSTGUI.this);
            }
        }));

        JMenu includedFiles = new JMenu("Included BST files");

        for(Entry<String, String> entry : OpenBST.getInternalFiles().entrySet())
        {
            jmi = new JMenuItem(entry.getKey());
            jmi.addActionListener(ev ->
            {
                String path = "/bst/" + entry.getValue();
                InputStream is = OpenBSTGUI.class.getResourceAsStream(path);
                ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(OpenBSTGUI.this,
                        "Extracting...", is);
                new Thread(() ->
                {
                    try
                    {
                        File f = File.createTempFile("openbstinternal", ".bsp");
                        FileOutputStream fos = new FileOutputStream(f);
                        IOUtils.copy(pmis, fos);
                        openStory(f);
                    }
                    catch(final IOException e)
                    {
                        LOG.error("IOException caught", e);
                        showException(
                                Lang.get("file.error").replace("$e", e.getClass().getSimpleName())
                                        .replace("$m", e.getMessage()),
                                e);
                    }

                }).start();

            });
            includedFiles.add(jmi);
        }
        additionalMenu.add(includedFiles);

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

        JMenu additionalLightThemesMenu = new JMenu(Lang.get("menu.themes.morelight"));
        int j = 3;
        for(Map.Entry<String, LookAndFeel> entry : ADDITIONAL_LIGHT_THEMES.entrySet())
        {
            int jf = j;
            jrbmi = new JRadioButtonMenuItem(entry.getKey());
            if(j == selectedTheme)
                jrbmi.setSelected(true);
            jrbmi.addActionListener(e -> switchLaF(jf, entry.getValue()));
            additionalLightThemesMenu.add(jrbmi);
            themesGroup.add(jrbmi);
            j++;
        }
        themesMenu.add(additionalLightThemesMenu);

        JMenu additionalDarkThemesMenu = new JMenu(Lang.get("menu.themes.moredark"));
        for(Map.Entry<String, LookAndFeel> entry : ADDITIONAL_DARK_THEMES.entrySet())
        {
            int jf = j;
            jrbmi = new JRadioButtonMenuItem(entry.getKey());
            if(j == selectedTheme)
                jrbmi.setSelected(true);
            jrbmi.addActionListener(e -> switchLaF(jf, entry.getValue()));
            additionalDarkThemesMenu.add(jrbmi);
            themesGroup.add(jrbmi);
            j++;
        }
        themesMenu.add(additionalDarkThemesMenu);

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

    ///////// FRAME MODIFICATION
    public void removeTab(final JPanel panel)
    {
        container.remove(panel);
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
        final StoryPanel sp = new StoryPanel(story, file, client);
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

    ///////// ACTIONS ON CLICKS
    private void doNewEditor()
    {
        try
        {
            StoryEditor se = new StoryEditor(new BranchingStory());
            container.addTab("Editor", se);
            container.setSelectedComponent(se);
        }
        catch(Exception e)
        {
            LOG.error("Error on story editor init", e);
            Messagers.showException(this, "Error while creating the Story Editor", e);
        }
    }

    ///////// OPENING STORIES
    public void openStory(File f)
    {
        if(f != null)
        {
            final TabClient client = new TabClient(instance);
            OpenBST.loadFile(f, client, new Consumer<BranchingStory>()
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
                                    showException(Lang.get("file.resourceerror")
                                            .replace("$e", whichCause(e))
                                            .replace("$m", whichMessage(e)), e);
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

    private void openEditor(File f)
    {
        if(f != null)
        {
            final TabClient client = new TabClient(instance);
            OpenBST.loadFile(f, client, new Consumer<BranchingStory>()
            {
                @Override
                public void accept(BranchingStory bs)
                {
                    try
                    {
                        SwingUtilities.invokeAndWait(() ->
                        {
                            try
                            {
                                StoryEditor se = new StoryEditor(bs);
                                container.addTab(se.getTitle(), se);
                                container.setSelectedComponent(se);
                            }
                            catch(Exception e)
                            {
                                LOG.error("Error on story editor init", e);
                                Messagers.showException(OpenBSTGUI.this,
                                        "Error while creating the Story Editor ("
                                                + e.getClass().getSimpleName() + " : "
                                                + e.getMessage() + ")",
                                        e);
                            }
                        });
                    }
                    catch(Exception e)
                    {
                        LOG.error(e);
                    }
                }
            });
        }
    }

    ////// UTILITIES

    private void switchLaF(int id, LookAndFeel laf)
    {
        try
        {
            dark = id == 0 || ADDITIONAL_DARK_THEMES.containsValue(laf);
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(instance);
            background.setDark(dark);
            darkModeCallbacks.forEach(a -> a.accept(dark));
            selectedTheme = id;
        }
        catch(UnsupportedLookAndFeelException e)
        {
            LOG.warn("Unsupported LaF", e);
        }
    }

    protected void showMessage(String msg, int type)
    {
        invokeSwingAndWait(() -> Messagers.showMessage(this, msg, type));
    }

    protected void showException(String msg, Exception e)
    {
        invokeSwingAndWait(() -> Messagers.showException(this, msg, e));
    }

    public static void addDarkModeCallback(Consumer<Boolean> callback)
    {
        darkModeCallbacks.add(callback);
    }

    public static void removeDarkModeCallbback(Consumer<Boolean> callback)
    {
        darkModeCallbacks.remove(callback);
    }

    public static Boolean isDark()
    {
        return dark;
    }

    @Override
    public void updateName(JPanel pan, String string)
    {
        setTabName(pan, string);
    }

    public void setTabName(JPanel panel, String string)
    {
        if(container.indexOfComponent(panel) != -1)
            container.setTitleAt(container.indexOfComponent(panel), string);
    }

    public void addBanner(JBannerPanel banner)
    {
        bannersPanel.add(banner, "grow");
        if(bannersPanel.getComponents().length > 2)
        {
            Component[] toScan = Arrays.copyOf(bannersPanel.getComponents(),
                    bannersPanel.getComponents().length);
            for(Component c : toScan)
            {
                if(c instanceof JBannerPanel)
                {
                    if(((JBannerPanel)c).isHideable())
                        bannersPanel.remove(c);
                    if(bannersPanel.getComponents().length <= 2)
                        break;
                }
            }
        }
        banner.revalidate();
        banner.repaint();

        // So... There's a weird bug where the background will keep some bits of older
        // banners that were present at first paint time.
        // This makes sure that after everything is rendered correctly and ready, the
        // background gets repainted fully
        // (swing is painful sometimes)
        SwingUtilities.invokeLater(() ->
        {
            background.revalidate();
            background.repaint();
        });
    }
}
