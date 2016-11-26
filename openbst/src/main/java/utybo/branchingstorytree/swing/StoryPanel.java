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
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringEscapeUtils;

import com.github.rjeschke.txtmark.Processor;
import com.google.gson.Gson;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.SaveState;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;
import utybo.branchingstorytree.swing.JScrollablePanel.ScrollableSizeHint;
import utybo.branchingstorytree.uib.UIBarHandler;

@SuppressWarnings("serial")
public class StoryPanel extends JPanel implements UIBarHandler
{

    protected BranchingStory story;
    private StoryNode currentNode;
    private TabClient client;
    private SaveState latestSaveState;
    private File file;

    protected OpenBST parentWindow;
    private final JLabel textLabel;
    private JLabel nodeIdLabel;
    private NodeOption[] options;
    private JButton[] optionsButton;
    private final JPanel panel = new JPanel();
    private Color normalButtonFg;

    private JButton restoreSaveStateButton, exportSaveStateButton;
    protected JToggleButton variableWatcherButton;
    protected VariableWatchDialog variableWatcher;

    public StoryPanel(BranchingStory story, OpenBST parentWindow, File f, TabClient client)
    {
        log("=> Initial setup");
        client.setStoryPanel(this);
        this.story = story;
        this.parentWindow = parentWindow;
        file = f;
        this.client = client;

        log("=> Creating visual elements");
        setLayout(new MigLayout("hidemode 3", "[grow]", ""));

        createToolbar();

        if(story.hasTag("uib_layout"))
        {
            uibPanel = new JPanel();
            add(uibPanel, "growx, wrap");
            uibPanel.setVisible(false);
        }

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, "grow, pushy, wrap");

        textLabel = new JLabel("<html><b>If you see this, there was a problem.</b><p>You may have seen a failure message. Please fix the problem and reload the file :)");
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setFont(new JTextArea().getFont());
        textLabel.setForeground(Color.BLACK);
        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        final JScrollablePanel jsp = new JScrollablePanel(new BorderLayout());
        jsp.add(textLabel, BorderLayout.CENTER);
        jsp.setScrollableWidth(ScrollableSizeHint.FIT);
        jsp.setScrollableHeight(ScrollableSizeHint.STRETCH);
        jsp.setBackground(Color.WHITE);
        scrollPane.setViewportView(jsp);

        add(panel, "growx");

        setupStory();
    }

    private void createToolbar()
    {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(new AbstractAction("Create Save State", new ImageIcon(OpenBST.saveAsImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                latestSaveState = new SaveState(currentNode.getId(), story.getRegistry());
                restoreSaveStateButton.setEnabled(true);
                exportSaveStateButton.setEnabled(true);
            }
        });
        restoreSaveStateButton = toolBar.add(new AbstractAction("Restore latest Save State", new ImageIcon(OpenBST.undoImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, "<html><body style='width: 300px'>You are about to go back to the latest save state. Are you sure you want to do this?", "Restore Save State confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.undoBigImage)) == JOptionPane.YES_OPTION)
                {
                    restoreSaveState(latestSaveState);
                }
            }
        });
        restoreSaveStateButton.setEnabled(false);
        exportSaveStateButton = toolBar.add(new AbstractAction("Export latest Save State", new ImageIcon(OpenBST.exportImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                final FileDialog jfc = new FileDialog(parentWindow, "Save State location", FileDialog.SAVE);
                jfc.setLocationRelativeTo(parentWindow);
                jfc.setIconImage(OpenBST.exportImage);
                jfc.setVisible(true);
                if(jfc.getFile() != null)
                {
                    File file = new File(jfc.getFile().endsWith(".bss") ? jfc.getDirectory() + jfc.getFile() : jfc.getDirectory() + jfc.getFile() + ".bss");
                    Gson gson = new Gson();
                    file.delete();
                    try
                    {
                        file.createNewFile();
                        FileWriter writer = new FileWriter(file);
                        gson.toJson(new SaveState(currentNode.getId(), story.getRegistry()), writer);
                        writer.flush();
                        writer.close();
                    }
                    catch(IOException e1)
                    {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(parentWindow, "Could not save the file : " + e1.getMessage() + " (" + e1.getClass().getSimpleName() + ")");
                    }
                }
            }
        });
        exportSaveStateButton.setEnabled(false);
        toolBar.add(new AbstractAction("Import Save State", new ImageIcon(OpenBST.importImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                final FileDialog jfc = new FileDialog(parentWindow, "Save State location", FileDialog.LOAD);
                jfc.setLocationRelativeTo(parentWindow);
                jfc.setIconImage(OpenBST.importImage);
                jfc.setVisible(true);
                if(jfc.getFile() != null)
                {
                    File file = new File(jfc.getDirectory() + jfc.getFile());
                    Gson gson = new Gson();
                    try
                    {
                        FileReader reader = new FileReader(file);
                        latestSaveState = gson.fromJson(reader, SaveState.class);
                        reader.close();
                        restoreSaveState(latestSaveState);
                    }
                    catch(IOException e1)
                    {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(parentWindow, "Could not save the file : " + e1.getMessage() + " (" + e1.getClass().getSimpleName() + ")");
                    }
                }
            }
        });
        toolBar.addSeparator();
        toolBar.add(new AbstractAction("Reset and restart from the beginning", new ImageIcon(OpenBST.returnImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, "<html><body style='width: 300px'>You are about to reset your progress and restart from the beginning. Are you sure you want to continue?", "Reset and Restart confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.returnBigImage)) == JOptionPane.YES_OPTION)
                {
                    reset();
                }
            }
        });
        toolBar.add(new AbstractAction("Soft Reload (reload the file and go back to where I left)", new ImageIcon(OpenBST.refreshImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, "<html><body style='width: 300px'>You are about to reload the BST file. We will try to restore where you were, but this is not a good idea if you heavily edited nodes and scripting. Are you sure you want to continue?", "Soft Reload confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.refreshBigImage)) == JOptionPane.YES_OPTION)
                {
                    SaveState ss = new SaveState(currentNode.getId(), story.getRegistry());
                    reload();
                    restoreSaveState(ss);
                }
            }
        });
        toolBar.add(new AbstractAction("Hard Reload (reload and reset)", new ImageIcon(OpenBST.synchronizeImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, "<html><body style='width: 300px'>You are about to reload the BST file. This will also reset all your progress. Are you sure you want to continue?", "Hard Reload confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.synchronizeBigImage)) == JOptionPane.YES_OPTION)
                {
                    reset();
                    reload();
                    reset();
                }
            }
        });
        toolBar.addSeparator();
        toolBar.add(new AbstractAction("Jump to Node...", new ImageIcon(OpenBST.jumpImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                SpinnerNumberModel model = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
                JSpinner spinner = new JSpinner(model);
                int i = JOptionPane.showOptionDialog(parentWindow, spinner, "Jump to Node number...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(OpenBST.jumpBigImage), null, null);
                if(i == JOptionPane.OK_OPTION)
                {
                    showNode(story.getNode((Integer)spinner.getModel().getValue()));
                }
            }
        });
        variableWatcherButton = new JToggleButton("", new ImageIcon(OpenBST.addonSearchImage));
        variableWatcherButton.addItemListener(e ->
        {
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                variableWatcher = new VariableWatchDialog(StoryPanel.this);
                variableWatcher.setVisible(true);
            }
            else if(e.getStateChange() == ItemEvent.DESELECTED)
            {
                variableWatchClosing();
            }
        });
        variableWatcherButton.setToolTipText("Variable watcher...");
        toolBar.add(variableWatcherButton);

        toolBar.addSeparator();

        nodeIdLabel = new JLabel("Please wait...");
        nodeIdLabel.setVerticalAlignment(SwingConstants.CENTER);
        nodeIdLabel.setEnabled(false);
        toolBar.add(nodeIdLabel);

        toolBar.addSeparator();

        JLabel hintLabel = new JLabel("Hover on one of the icons for more details.");
        hintLabel.setEnabled(false);
        toolBar.add(hintLabel);

        toolBar.add(Box.createHorizontalGlue());

        toolBar.addSeparator();

        toolBar.add(new AbstractAction("Close tab", new ImageIcon(OpenBST.closeImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, "<html><body style='width: 300px'>You are about to close this BST file. All unsaved progress will be lost. Are you sure you want to close this tab?", "Tab close confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.closeBigImage)) == JOptionPane.YES_OPTION)
                {
                    parentWindow.removeStory(StoryPanel.this);
                }
            }
        });

        for(Component component : toolBar.getComponents())
        {
            if(component instanceof JButton)
            {
                ((JButton)component).setHideActionText(false);
                ((JButton)component).setToolTipText(((JButton)component).getText());
                ((JButton)component).setText("");
            }
        }
        add(toolBar, "growx, wrap");
    }

    protected void restoreSaveState(SaveState ss)
    {
        ss.applySaveState(story);
        showNode(story.getNode(ss.getNodeId()));
    }

    private void setupStory()
    {
        log("=> Analyzing options and deducing maximum option amount");
        // Quick analysis of all the nodes to get the maximum amount of options
        int maxOptions = 0;
        for(final StoryNode sn : story.getAllNodes())
        {
            if(sn instanceof TextNode && ((TextNode)sn).getOptions().size() > maxOptions)
            {
                maxOptions = ((TextNode)sn).getOptions().size();
            }
        }
        if(maxOptions < 4)
        {
            maxOptions = 4;
        }
        int rows = maxOptions / 2;
        // Make sure the options are always a multiple of 2
        if(maxOptions % 2 == 1)
        {
            rows++;
        }
        options = new NodeOption[rows * 2];
        optionsButton = new JButton[rows * 2];
        panel.removeAll();
        panel.setLayout(new GridLayout(rows, 2, 5, 5));
        for(int i = 0; i < options.length; i++)
        {
            final int optionId = i;
            final JButton button = new JButton();
            normalButtonFg = button.getForeground();
            button.addActionListener(ev ->
            {
                try
                {
                    optionSelected(options[optionId]);
                }
                catch(final BSTException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error on node " + currentNode.getId() + " :" + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(button);
            optionsButton[i] = button;
            button.setEnabled(false);
        }

        log("Displaying first node");
        showNode(story.getInitialNode());
    }

    protected void reload()
    {
        story = parentWindow.loadFile(file, client);
        setupStory();
    }

    private void showNode(final StoryNode storyNode)
    {
        if(storyNode == null)
        {
            // The node does not exist
            log("=! Node launched does not exist");
            if(currentNode == null)
            {
                log("=! It was the initial node");
                JOptionPane.showMessageDialog(this, "The initial node does not exist. Make sure you are using a correct BST file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "The node that was supposed to be opened does not exist. Current node : " + currentNode.getId(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        log("=> Trying to show node : " + storyNode.getId());

        currentNode = storyNode;
        nodeIdLabel.setText("Node : " + currentNode.getId());

        try
        {
            // If this is a LogicalNode, we need to solve it.
            if(storyNode instanceof LogicalNode)
            {
                log("=> Solving logical node");
                final int i = ((LogicalNode)storyNode).solve();
                log("=> Logical node result : " + i);
                // TODO Throw a nicer exception when an invalid value is returned
                showNode(story.getNode(i));
            }

            // This is supposed to be executed when the StoryNode is a TextNode
            if(storyNode instanceof TextNode)
            {
                log("=> Text node detected");
                final TextNode textNode = (TextNode)storyNode;

                log("=> Solving variables");
                String text = StoryUtils.solveVariables(textNode, story);

                log("=> Solving markup language");
                // Process the markup language
                // 0 == none
                // 1 == Markdown
                // 2 == HTML
                final int markupLanguage = solveMarkup(textNode);
                text = translateMarkup(markupLanguage, text);

                log("=> Applying text");
                textLabel.setText(text);

                if(textNode.hasTag("color"))
                {
                    log("=> Tag 'color' found");
                    final String color = textNode.getTag("color");
                    Color c = null;
                    if(color.startsWith("#"))
                    {
                        log("=> Hex color found, parsing");
                        c = new Color(Integer.parseInt(color.substring(1), 16));
                    }
                    else
                    {
                        log("=> Trying to parse normal color : " + color);
                        try
                        {
                            c = (Color)Color.class.getField(color).get(null);
                        }
                        catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
                        {
                            log("=! Color does not exist!");
                            System.err.println("COLOR DOES NOT EXIST : " + color);
                            e.printStackTrace();
                        }
                    }
                    if(c != null)
                    {
                        log("=> Applying custom color");
                        textLabel.setForeground(c);
                    }
                    else
                    {
                        log("=> Applying default color");
                        textLabel.setForeground(Color.BLACK);
                    }
                }
                else
                {
                    log("=> Tag 'color' not found, applying default color");
                    textLabel.setForeground(Color.BLACK);
                }
                log("Resetting options");
                resetOptions();

                log("Applying options for node : " + textNode.getId());
                showOptions(textNode);

                log("Updating UIB if necessary");
                updateUIB();
            }
        }
        catch(final Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error on node " + storyNode.getId() + " :" + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String translateMarkup(int markupLanguage, String input)
    {
        switch(markupLanguage)
        {
        case 1:
            log("=> Processing markup language : Markdown");
            return "<html>" + Processor.process(input); // MD to HTML
        // TODO Test to see if HTML characters are escaped
        case 2:
            log("=> Processing markup language : HTML");
            return "<html>" + input; // HTML to HTML
        default:
            log("=> Processing markup language : None");
            return "<html>" + StringEscapeUtils.escapeHtml(input).replace("\n", "<br>"); // Plain text to HTML
        }
    }

    private void showOptions(final TextNode textNode) throws BSTException
    {
        log("=> Filtering valid options");
        final ArrayList<NodeOption> validOptions = new ArrayList<>();
        for(final NodeOption no : textNode.getOptions())
        {
            if(no.getChecker().check())
            {
                validOptions.add(no);
            }
        }
        if(validOptions.size() > 0)
        {
            log("=> Valid options found (" + validOptions.size() + " valid on " + textNode.getOptions().size() + " total)");
            log("=> Processing options");
            for(int i = 0; i < validOptions.size(); i++)
            {
                final NodeOption option = validOptions.get(i);
                final JButton button = optionsButton[i];
                options[i] = option;
                button.setEnabled(true);
                if(i == 0)
                    button.requestFocus();
                if(option.hasTag("color"))
                {
                    final String color = option.getTag("color");
                    Color c = null;
                    if(color.startsWith("#"))
                    {
                        c = new Color(Integer.parseInt(color.substring(1), 16));
                    }
                    else
                    {
                        try
                        {
                            c = (Color)Color.class.getField(color).get(null);
                        }
                        catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
                        {
                            System.err.println("COLOR DOES NOT EXIST : " + color);
                            e.printStackTrace();
                        }
                    }
                    if(c != null)
                    {
                        button.setForeground(c);
                    }
                }
                button.setText(option.getText());
            }
        }
        else
        {
            log("=> No valid options found (" + validOptions.size() + " total");
            log("=> Shwoing ending");
            optionsButton[0].setText("The End.");
            optionsButton[1].setText("Final node : " + textNode.getId());
            optionsButton[2].setText("Restart");
            optionsButton[2].setEnabled(true);
            optionsButton[2].requestFocus();
            final ActionListener[] original = optionsButton[2].getActionListeners();
            final ActionListener[] original2 = optionsButton[3].getActionListeners();
            for(final ActionListener al : original)
            {
                optionsButton[2].removeActionListener(al);
            }
            final ActionListener shutdownListener = e -> parentWindow.removeStory(this);
            optionsButton[2].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    log("Resetting story");
                    for(final ActionListener al : original)
                    {
                        optionsButton[2].addActionListener(al);
                    }
                    for(final ActionListener al : original2)
                    {
                        optionsButton[3].addActionListener(al);
                    }
                    optionsButton[2].removeActionListener(this);
                    optionsButton[3].removeActionListener(shutdownListener);
                    reset();
                }
            });
            optionsButton[3].setText("Close");
            optionsButton[3].setEnabled(true);
            for(final ActionListener al : original2)
            {
                optionsButton[3].removeActionListener(al);
            }
            optionsButton[3].addActionListener(shutdownListener);
        }

    }

    private void reset()
    {
        log("=> Performing internal reset");
        story.reset();

        resetUib();

        log("=> Processing initial node again");
        showNode(story.getInitialNode());
    }

    private int solveMarkup(final TextNode textNode)
    {
        if(story.hasTag("markup") || (textNode != null && textNode.hasTag("markup")))
        {
            if(textNode != null && textNode.hasTag("markup"))
            {
                final String s = textNode.getTag("markup");
                if(s.equalsIgnoreCase("md") || s.equalsIgnoreCase("markdown"))
                {
                    return 1;
                }
                else if(s.equalsIgnoreCase("html"))
                {
                    return 2;
                }
            }
            else if(story.hasTag("markup"))
            {
                final String s = story.getTag("markup");
                if(s.equalsIgnoreCase("md") || s.equalsIgnoreCase("markdown"))
                {
                    return 1;
                }
                else if(s.equalsIgnoreCase("html"))
                {
                    return 2;
                }
            }
        }
        return 0;
    }

    private void resetOptions()
    {
        for(int i = 0; i < optionsButton.length; i++)
        {
            options[i] = null;
            final JButton button = optionsButton[i];
            button.setForeground(normalButtonFg);
            button.setEnabled(false);
            button.setText("");
        }
    }

    private void optionSelected(final NodeOption nodeOption) throws BSTException
    {
        for(final ActionDescriptor oa : nodeOption.getDoOnClickActions())
        {
            oa.exec();
        }
        showNode(story.getNode(nodeOption.getNextNode()));
    }

    public static void log(String message)
    {
        // TODO Add a better logging system
        System.out.println(message);
    }

    public String getTitle()
    {
        HashMap<String, String> tagMap = story.getTagMap();
        String s = "";
        s += tagMap.getOrDefault("title", "<undefined>");
        s += " - ";
        s += tagMap.getOrDefault("author", "<unknown>");
        return s;
    }

    public boolean postCreation()
    {
        log("Issuing NSFW warning");
        if(story.hasTag("nsfw") && JOptionPane.showConfirmDialog(this, "<html><b>WARNING</b><p>You are about to read a NSFW story. This story is not suitable for children.<p>Only click OK if you are OVER 18 YEARS OLD.", "NSFW WARNING", JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
        {
            log("=> Close");
            return false;
        }
        nodeIdLabel.setForeground(Color.RED);
        return true;
    }

    protected void variableWatchClosing()
    {
        variableWatcherButton.setSelected(false);
        variableWatcher.deathNotified();
        variableWatcher.dispose();
    }

    // --------------------- UIB --------------------- //

    private String layout;
    private JPanel uibPanel;
    private TreeMap<String, JComponent> uibComponents = new TreeMap<>();
    private boolean uibInitialized = false;

    @Override
    public void setLayout(String layoutIdentifier) throws BSTException
    {
        layout = layoutIdentifier;
    }

    @Override
    public void initialize() throws BSTException
    {
        uibInitialized = true;
        story.putTag("__uib__initialized", "true");
        boolean gridMode = story.hasTag("uib_grid");
        String columnDef = story.getTag("uib_grid");
        boolean advancedMode = Boolean.parseBoolean(story.getTag("uib_advanced"));
        uibPanel.setLayout(new MigLayout((gridMode ? "" : "nogrid, ") + "fillx", columnDef));
        String[] parts = layout.split(advancedMode ? ";" : "[,;]");
        boolean newLine = false;
        for(int i = 0; i < parts.length; i++)
        {
            String s = parts[i];
            int index = s.indexOf(':');
            String comp = index > -1 ? s.substring(0, index) : s;
            String constraints = advancedMode && index > -1 ? s.substring(index + 1) : "";
            Component toAdd = null;
            switch(comp)
            {
            case "tb":
                JLabel t = new JLabel();
                uibComponents.put(determineNext("t"), t);
                uibPanel.add(t, (newLine ? "newline" : "") + ", alignx right");
                newLine = false;
                // $FALL-THROUGH$
            case "b":
                JProgressBar jpb = new JProgressBar();
                uibComponents.put(determineNext("b"), jpb);
                toAdd = jpb;
                if(!advancedMode)
                    constraints += ", grow";
                uibPanel.add(jpb);
                break;
            case "t":
                JLabel t1 = new JLabel();
                uibComponents.put(determineNext("t"), t1);
                constraints += ", aligny top";
                toAdd = t1;
                break;
            case "vs":
                toAdd = new JSeparator(SwingConstants.VERTICAL);
                constraints += ", growy";
                break;
            case "hs":
                toAdd = new JSeparator(SwingConstants.HORIZONTAL);
                constraints += ", growx";
                break;
            case "nl":
                // $FALL-THROUGH$
            case "ln":
                newLine = true;
                continue; // Get out of here! we don't have anything to add
            case "gu":
                toAdd = Box.createHorizontalStrut(5);
                break;
            default:
                throw new BSTException(-1, "Unknown component : '" + comp + "'");
            }
            if(newLine)
            {
                constraints += ", newline";
            }
            uibPanel.add(toAdd, constraints);
        }
        uibPanel.revalidate();
    }

    private String determineNext(String string)
    {
        int i = 0;
        Pattern p = Pattern.compile(string + "\\d+");
        for(String s : uibComponents.keySet())
        {
            if(p.matcher(s).matches())
            {
                i++;
            }
        }
        return string + i;
    }

    @Override
    public boolean isElementValueTypeInteger(String element) throws BSTException
    {
        JComponent c = uibComponents.get(element);
        return c != null && c instanceof JProgressBar;
    }

    @Override
    public void setElementValue(String element, int value) throws BSTException
    {
        JComponent c = uibComponents.get(element);
        if(c != null)
        {
            story.putTag("__uib__" + element + "_value", "" + value);
            updateComponent(element, c);
        }
    }

    @Override
    public void setElementValue(String element, String value) throws BSTException
    {
        JComponent c = uibComponents.get(element);
        if(c != null)
        {
            story.putTag("__uib__" + element + "_value", "" + value);
            updateComponent(element, c);
        }
    }

    @Override
    public void setElementMax(String element, int max)
    {
        JComponent c = uibComponents.get(element);
        if(c instanceof JProgressBar)
        {
            story.putTag("__uib__" + element + "_max", "" + max);
            ((JProgressBar)c).getModel().setMaximum(max);
        }

    }

    @Override
    public void setElementMin(String element, int min)
    {
        JComponent c = uibComponents.get(element);
        if(c instanceof JProgressBar)
        {
            story.putTag("__uib__" + element + "_min", "" + min);
            ((JProgressBar)c).getModel().setMinimum(min);
        }

    }

    @Override
    public boolean supportsDynamicInteger(String element)
    {
        JComponent c = uibComponents.get(element);
        return c instanceof JProgressBar;
    }

    @Override
    public void setUIBVisisble(boolean parseBoolean)
    {
        uibPanel.setVisible(parseBoolean);
        story.putTag("__uib__visible", Boolean.toString(parseBoolean));
    }

    @Override
    public boolean elementExists(String element)
    {
        return uibComponents.containsKey(element);
    }

    public void updateUIB() throws BSTException
    {
        if(uibInitialized)
        {
            for(String element : uibComponents.keySet())
            {
                updateComponent(element, uibComponents.get(element));
            }
        }
    }

    private void updateComponent(String element, JComponent c) throws BSTException
    {
        if(c instanceof JLabel)
        {
            String s = translateMarkup(solveMarkup(null), computeText(story.getTag("__uib__" + element + "_value")));
            //String s = computeText(uibch.getValue().toString());
            ((JLabel)c).setText(s);
        }
        else if(c instanceof JProgressBar)
        {
            ((JProgressBar)c).setValue(computeInt(story.getTag("__uib__" + element + "_value")));
        }

    }

    private int computeInt(String value)
    {
        if(value == null)
            return 0;

        VariableRegistry registry = story.getRegistry();
        try
        {
            return registry.typeOf(value) == Integer.class ? (Integer)registry.get(value, 0) : Integer.parseInt(value);
        }
        catch(NumberFormatException nfe2)
        {
            return 0;
        }
    }

    private String computeText(String string) throws BSTException
    {
        if(string == null)
            return "";
        try
        {
            if(string.startsWith(">"))
            {
                return computeText(Integer.valueOf(string.substring(1)), true);
            }
            else if(string.startsWith("&"))
            {
                return computeText(Integer.valueOf(string.substring(1)), false);
            }
        }
        catch(NumberFormatException e)
        {}
        return string;
    }

    private String computeText(int i, boolean isVirtual) throws BSTException
    {
        if(isVirtual)
        {
            return StoryUtils.solveVariables((VirtualNode)story.getNode(i), story);
        }
        else
        {
            int j = ((LogicalNode)story.getNode(i)).solve();
            return computeText(j, story.getNode(j) instanceof LogicalNode ? false : true);
        }
    }

    @Override
    public void restoreState() throws BSTException
    {
        // While save states aim to be restored at a minimum cost,
        // UIB will be reset when restored. This is to avoid glitches
        // and make sure we build onto a clean UIB.
        resetUib();

        if(Boolean.parseBoolean("__uib__initialized"))
        {
            // Initialize
            initialize();

            // Restore all values
            for(String element : uibComponents.keySet())
            {
                Map<String, String> componentTags = getUibTags(element);

                for(String tag : componentTags.keySet())
                {
                    String value = componentTags.get(tag);
                    switch(tag)
                    {
                    case "max":
                        setElementMax(element, Integer.parseInt(value));
                        break;
                    case "min":
                        setElementMin(element, Integer.parseInt(value));
                        break;
                    case "value":
                        setElementValue(element, value);
                        break;
                    }
                }
            }

            // Redefine if visible or not
            if("true".equals(story.getTag("__uib__visible")))
            {

            }
        }
    }

    private Map<String, String> getUibTags(String element)
    {
        HashMap<String, String> map = new HashMap<>();
        for(String tagName : story.getTagMap().keySet())
        {
            if(tagName.startsWith("__uib__" + element + "_"))
            {
                map.put(tagName.substring("__uib__".length() + element.length() + "_".length()), map.get(tagName));
            }
        }
        return map;
    }

    public void resetUib()
    {
        log("=> Performing UIB Reset");
        uibPanel.removeAll();
        uibPanel.revalidate();
        uibPanel.repaint();
        uibPanel.setVisible(false);
        uibInitialized = false;
        uibComponents.clear();
    }
}
