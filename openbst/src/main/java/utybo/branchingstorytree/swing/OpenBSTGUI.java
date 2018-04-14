/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import static utybo.branchingstorytree.swing.OpenBST.LOG;
import static utybo.branchingstorytree.swing.VisualsUtils.invokeSwingAndWait;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

import org.apache.commons.io.FilenameUtils;
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
import org.pushingpixels.substance.swingx.SubstanceSwingxPlugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.editor.StoryEditor;
import utybo.branchingstorytree.swing.impl.BRMFileClient;
import utybo.branchingstorytree.swing.impl.IMGClient;
import utybo.branchingstorytree.swing.impl.TabClient;
import utybo.branchingstorytree.swing.utils.BSTPackager;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.Lang.UnrespectedModelException;
import utybo.branchingstorytree.swing.visuals.AboutDialog;
import utybo.branchingstorytree.swing.visuals.JBackgroundPanel;
import utybo.branchingstorytree.swing.visuals.JBannerPanel;
import utybo.branchingstorytree.swing.visuals.PackageDialog;
import utybo.branchingstorytree.swing.visuals.StoryPanel;

@SuppressWarnings("serial")
public class OpenBSTGUI extends JFrame
{
    private static OpenBSTGUI instance;
    private final BranchingStoryTreeParser parser = new BranchingStoryTreeParser();
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

    private int selectedTheme = 1;
    private boolean dark = false;
    private final LinkedList<Consumer<Boolean>> darkModeCallbacks = new LinkedList<>();

    public static void launch()
    {
        VisualsUtils.invokeSwingAndWait(() ->
        {
            instance = new OpenBSTGUI();
            instance.setVisible(true);
        });
    }

    public static OpenBSTGUI getInstance()
    {
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
                SubstanceCortex.GlobalScope.registerComponentPlugin(new SubstanceSwingxPlugin());

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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
                    Color.ORANGE,
                    "You are running a Snapshot. This version is in development. Do not post bug reports!",
                    null, false), "grow");
        }

        if(System.getProperty("java.specification.version").equals("9"))
        {
            bannersPanel.add(
                    new JBannerPanel(new ImageIcon(Icons.getImage("Attention", 32)),
                            new Color(255, 50, 50), Lang.get("welcome.java9warning"), null, false),
                    "grow");
        }
        if(System.getProperty("java.specification.version").equals("10"))
        {
            bannersPanel.add(
                    new JBannerPanel(new ImageIcon(Icons.getImage("Attention", 32)),
                            new Color(255, 50, 50), Lang.get("welcome.java10warning"), null, false),
                    "grow");
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

        final JButton btnOpenEditor = new JButton("Open Editor");
        panel.add(btnOpenEditor, "cell 4 1");
        btnOpenEditor.setIcon(new ImageIcon(Icons.getImage("Open", 40)));
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

        setSize((int)(830 * Icons.getScale()), (int)(480 * Icons.getScale()));
        setLocationRelativeTo(null);
    }

    private JMenu createShortMenu()
    {
        JMenu shortMenu = new JMenu();
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
        shortMenu.add(new JMenuItem(
                new AbstractAction(Lang.get("menu.open"), new ImageIcon(Icons.getImage("Open", 16)))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        openStory(VisualsUtils.askForFile(OpenBSTGUI.this, Lang.get("file.title")));
                    }
                }));

        shortMenu.addSeparator();

        shortMenu.add(new JMenuItem(new AbstractAction("Edit a new story")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewEditor();
            }
        }));

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

        additionalMenu.add(new JMenuItem(new AbstractAction("Show debug info")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                DebugInfo.launch(OpenBSTGUI.this);
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

        JMenu additionalLightThemesMenu = new JMenu("Additional light themes");
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

        JMenu additionalDarkThemesMenu = new JMenu("Additional dark themes");
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
                                    if(Boolean.parseBoolean(
                                            bs.getTagOrDefault("img_requireinternal", "false")))
                                        IMGClient.initInternal();
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
            loadFile(f, client, new Consumer<BranchingStory>()
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
                    showException(Lang.get("file.error").replace("$e", e.getClass().getSimpleName())
                            .replace("$m", e.getMessage()), e);
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
                    if(doAndReturn(() -> Messagers.showConfirm(instance, s2,
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
                    showException(Lang.get("file.crash"), e);
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
                    Messagers.showException(instance, Lang.get("file.crash"), e);
                }
            }
        };
        worker.execute();
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
