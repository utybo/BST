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
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.google.gson.Gson;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.SaveState;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.JScrollablePanel.ScrollableSizeHint;

@SuppressWarnings("serial")
public class StoryPanel extends JPanel
{
    /**
     * The story represented by this StoryPanel. This variable will change if
     * the file is reload.
     */
    protected BranchingStory story;

    /**
     * The panel for use with the UIB Module
     */
    protected JPanel uibPanel;

    /**
     * The node currently on screen
     */
    private StoryNode currentNode;

    /**
     * The {@link TabClient} linked to this panel
     */
    private TabClient client;

    /**
     * The latest save state made - this is used with the save state buttons
     */
    private SaveState latestSaveState;

    /**
     * The file this story is from
     */
    private File bstFile;

    /**
     * The OpenBST window
     */
    protected OpenBST parentWindow;

    /**
     * The node panel the text is displayed in
     */
    private final NodePanel nodePanel;

    /**
     * The label displaying the current node ID.
     */
    private JLabel nodeIdLabel;

    /**
     * The list of node options for the current node
     */
    private NodeOption[] options;

    /**
     * An array of the Option Buttons available in the grid
     */
    private JButton[] optionsButton;

    /**
     * The panel in which option buttons are placed
     */
    private final JPanel optionPanel = new JPanel();

    /**
     * The regular foreground for option buttons when the "color" tag is not
     * applied
     */
    private Color normalButtonFg;

    // --- Toolbar buttons ---
    private JButton restoreSaveStateButton, exportSaveStateButton;
    protected JToggleButton variableWatcherButton;
    protected VariableWatchDialog variableWatcher;
    private JButton backgroundButton;

    /**
     * Initialize the story panel
     * 
     * @param story
     *            the story to create
     * @param parentWindow
     *            the OpenBST instance we are creating this story from
     * @param f
     *            the file the story is from
     * @param client
     *            the client that will be linked to this story
     */
    public StoryPanel(BranchingStory story, OpenBST parentWindow, File f, TabClient client)
    {
        log("=> Initial setup");
        bstFile = f;
        client.setStoryPanel(this);
        this.story = story;
        this.parentWindow = parentWindow;
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

        nodePanel = new NodePanel(client.getIMGHandler());
        nodePanel.setScrollableWidth(ScrollableSizeHint.FIT);
        nodePanel.setScrollableHeight(ScrollableSizeHint.STRETCH);
        scrollPane.setViewportView(nodePanel);

        add(optionPanel, "growx");

        setupStory();
    }

    /**
     * Create the toolbar, enforcing the supertools tag
     */
    private void createToolbar()
    {
        int toolbarLevel = readToolbarLevel();
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        if(toolbarLevel > 0)
        {
            toolBar.add(new AbstractAction(Lang.get("story.createss"), new ImageIcon(OpenBST.saveAsImage))
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    latestSaveState = new SaveState(currentNode.getId(), story.getRegistry());
                    restoreSaveStateButton.setEnabled(true);
                    if(exportSaveStateButton != null)
                        exportSaveStateButton.setEnabled(true);
                }
            });
            restoreSaveStateButton = toolBar.add(new AbstractAction(Lang.get("story.restoress"), new ImageIcon(OpenBST.undoImage))
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.restoress.confirm"), Lang.get("story.restoress"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.undoBigImage)) == JOptionPane.YES_OPTION)
                    {
                        restoreSaveState(latestSaveState);
                    }
                }
            });
            restoreSaveStateButton.setEnabled(false);
            if(toolbarLevel > 1)
            {
                exportSaveStateButton = toolBar.add(new AbstractAction(Lang.get("story.exportss"), new ImageIcon(OpenBST.exportImage))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        final FileDialog jfc = new FileDialog(parentWindow, Lang.get("story.sslocation"), FileDialog.SAVE);
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
                                JOptionPane.showMessageDialog(parentWindow, Lang.get("story.exportss.error").replace("$m", e1.getMessage()).replace("$e", e1.getClass().getSimpleName()));
                            }
                        }
                    }
                });
                exportSaveStateButton.setEnabled(false);
                toolBar.add(new AbstractAction(Lang.get("story.importss"), new ImageIcon(OpenBST.importImage))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        final FileDialog jfc = new FileDialog(parentWindow, Lang.get("story.sslocation"), FileDialog.LOAD);
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
                                JOptionPane.showMessageDialog(parentWindow, Lang.get("story.exportss.error").replace("$m", e1.getMessage()).replace("$e", e1.getClass().getSimpleName()));
                            }
                        }
                    }
                });
                if(toolbarLevel > 2)
                {
                    toolBar.addSeparator();
                    toolBar.add(new AbstractAction(Lang.get("story.reset"), new ImageIcon(OpenBST.returnImage))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.reset.confirm"), Lang.get("story.reset"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.returnBigImage)) == JOptionPane.YES_OPTION)
                            {
                                reset();
                            }
                        }
                    });
                    toolBar.add(new AbstractAction(Lang.get("story.sreload"), new ImageIcon(OpenBST.refreshImage))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.sreload.confirm"), Lang.get("story.sreload.confirm.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.refreshBigImage)) == JOptionPane.YES_OPTION)
                            {
                                SaveState ss = new SaveState(currentNode.getId(), story.getRegistry());
                                reset();
                                reload();
                                reset();
                                restoreSaveState(ss);
                            }
                        }
                    });
                    toolBar.add(new AbstractAction(Lang.get("story.hreload"), new ImageIcon(OpenBST.synchronizeImage))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.hreload.confirm"), Lang.get("story.hreload.confirm.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.synchronizeBigImage)) == JOptionPane.YES_OPTION)
                            {
                                reset();
                                reload();
                                reset();
                            }
                        }
                    });
                    if(toolbarLevel > 3)
                    {
                        toolBar.addSeparator();
                        toolBar.add(new AbstractAction(Lang.get("story.jumptonode"), new ImageIcon(OpenBST.jumpImage))
                        {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                SpinnerNumberModel model = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
                                JSpinner spinner = new JSpinner(model);
                                int i = JOptionPane.showOptionDialog(parentWindow, spinner, Lang.get("story.jumptonode"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(OpenBST.jumpBigImage), null, null);
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
                        variableWatcherButton.setToolTipText(Lang.get("story.variablewatcher"));
                        toolBar.add(variableWatcherButton);

                        toolBar.addSeparator();

                        nodeIdLabel = new JLabel(Lang.get("wait"));
                        nodeIdLabel.setVerticalAlignment(SwingConstants.CENTER);
                        nodeIdLabel.setEnabled(false);
                        toolBar.add(nodeIdLabel);
                    }
                }
            }

            toolBar.addSeparator();

            JLabel hintLabel = new JLabel(Lang.get("story.tip"));
            hintLabel.setEnabled(false);
            toolBar.add(hintLabel);
        }

        toolBar.add(Box.createHorizontalGlue());

        toolBar.addSeparator();

        backgroundButton = toolBar.add(new AbstractAction(Lang.get("story.seebackground"), new ImageIcon(OpenBST.pictureImage))
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                JDialog dialog = new JDialog(parentWindow);
                dialog.add(new JPanel()
                {
                    @Override
                    protected void paintComponent(Graphics g)
                    {
                        super.paintComponent(g);
                        BufferedImage image = client.getIMGHandler().getCurrentBackground();
                        double scaleFactor = 1d;
                        if(image.getWidth() > image.getHeight())
                        {
                            scaleFactor = getScaleFactorToFit(new Dimension(image.getWidth(), image.getHeight()), getParent().getSize());
                        }
                        else if(image.getHeight() > image.getWidth())
                        {
                            scaleFactor = getScaleFactorToFit(new Dimension(image.getWidth(), image.getHeight()), getParent().getSize());
                        }
                        int scaleWidth = (int)Math.round(image.getWidth() * scaleFactor);
                        int scaleHeight = (int)Math.round(image.getHeight() * scaleFactor);

                        Image scaled = image.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

                        int width = getWidth() - 1;
                        int height = getHeight() - 1;

                        int x = (width - scaled.getWidth(this)) / 2;
                        int y = (height - scaled.getHeight(this)) / 2;

                        g.drawImage(scaled, x, y, this);
                    }

                    private double getScaleFactorToFit(Dimension masterSize, Dimension targetSize)
                    {
                        double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
                        double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height);
                        double dScale = Math.min(dScaleHeight, dScaleWidth);
                        return dScale;
                    }

                    private double getScaleFactor(int iMasterSize, int iTargetSize)
                    {
                        double dScale = 1;
                        if(iMasterSize > iTargetSize)
                        {
                            dScale = (double)iTargetSize / (double)iMasterSize;
                        }
                        else
                        {
                            dScale = (double)iTargetSize / (double)iMasterSize;
                        }
                        return dScale;
                    }
                });
                dialog.setTitle(Lang.get("story.background"));
                dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                dialog.setIconImage(OpenBST.pictureImage);
                dialog.setSize(300, 300);
                dialog.setLocationRelativeTo(parentWindow);
                dialog.setVisible(true);
            }
        });

        JToggleButton muteButton = new JToggleButton("", new ImageIcon(OpenBST.speakerImage));
        muteButton.addActionListener(e ->
        {
            SSBClient ssb = client.getSSBHandler();
            if(ssb != null)
            {
                ssb.setMuted(muteButton.isSelected());
                muteButton.setIcon(new ImageIcon(muteButton.isSelected() ? OpenBST.muteImage : OpenBST.speakerImage));
            }
        });
        muteButton.setToolTipText(Lang.get("story.mute"));
        toolBar.add(muteButton);

        toolBar.add(new AbstractAction(Lang.get("story.close"), new ImageIcon(OpenBST.closeImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.close.confirm"), Lang.get("story.close"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(OpenBST.closeBigImage)) == JOptionPane.YES_OPTION)
                {
                    client.getSSBHandler().shutdown();
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

    /**
     * Read the toolbar level
     * 
     * @return an integer that represents the level specified in the
     *         "supertools" tag
     */
    private int readToolbarLevel()
    {
        String value = story.getTag("supertools");
        if(value == null)
            return 4;
        switch(value)
        {
        case "all":
            return 4;
        case "hidecheat":
            return 3;
        case "savestate":
            return 2;
        case "savestatenoio":
            return 1;
        case "none":
            return 0;
        default:
            return 0;
        }
    }

    /**
     * Restore a previous save state by applying it to the story first and
     * showing the node stored
     * 
     * @param ss
     */
    protected void restoreSaveState(SaveState ss)
    {
        ss.applySaveState(story);
        showNode(story.getNode(ss.getNodeId()));
        // Also notify modules that need to restore their state
        client.getSSBHandler().restoreSaveState();
        client.getIMGHandler().restoreSaveState();
        try
        {
            client.getUIBarHandler().restoreState();
        }
        catch(BSTException e)
        {
            // TODO Indicate this exception happened
            e.printStackTrace();
        }
    }

    /**
     * Setup of the story. This can be ran again if the story changed
     */
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
        optionPanel.removeAll();
        optionPanel.setLayout(new GridLayout(rows, 2, 5, 5));
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
                    JOptionPane.showMessageDialog(this, Lang.get("story.error").replace("$n", "" + currentNode.getId()).replace("$m", e.getMessage()), Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                }
            });
            optionPanel.add(button);
            optionsButton[i] = button;
            button.setEnabled(false);
        }

        log("Displaying first node");
        showNode(story.getInitialNode());
    }

    /**
     * Reload the file (not clean, this is a subroutine method part of the
     * entire reloading process)
     */
    protected void reload()
    {
        story = parentWindow.loadFile(bstFile, client);
        setupStory();
    }

    /**
     * Show a specific node
     * 
     * @param storyNode
     *            the node to show
     */
    private void showNode(final StoryNode storyNode)
    {
        if(storyNode == null)
        {
            // The node does not exist
            log("=! Node launched does not exist");
            if(currentNode == null)
            {
                log("=! It was the initial node");
                JOptionPane.showMessageDialog(this, Lang.get("story.missinginitial"), Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
            {
                JOptionPane.showMessageDialog(this, Lang.get("story.missingnode").replace("$n", "" + currentNode.getId()), Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        log("=> Trying to show node : " + storyNode.getId());

        currentNode = storyNode;
        if(nodeIdLabel != null)
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

                log("=> Applying text");
                nodePanel.applyNode(story, textNode);

                log("Resetting options");
                resetOptions();

                log("Applying options for node : " + textNode.getId());
                showOptions(textNode);

                log("Updating UIB if necessary");
                client.getUIBarHandler().updateUIB();

                backgroundButton.setEnabled(client.getIMGHandler().getCurrentBackground() != null);
            }
        }
        catch(final Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, Lang.get("story.error").replace("$n", "" + currentNode.getId()).replace("$m", e.getMessage()), Lang.get("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The options to show
     * 
     * @param textNode
     * @throws BSTException
     */
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
            optionsButton[0].setText(Lang.get("story.final.end"));
            optionsButton[1].setText(Lang.get("story.final.node").replace("$n", "" + textNode.getId()));
            optionsButton[2].setText(Lang.get("story.final.restart"));
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
            optionsButton[3].setText(Lang.get("story.final.close"));
            optionsButton[3].setEnabled(true);
            for(final ActionListener al : original2)
            {
                optionsButton[3].removeActionListener(al);
            }
            optionsButton[3].addActionListener(shutdownListener);
        }

    }

    /**
     * Internal reset (subroutine method)
     */
    private void reset()
    {
        log("=> Performing internal reset");
        story.reset();

        client.getUIBarHandler().resetUib();
        client.getIMGHandler().reset();
        client.getSSBHandler().reset();

        log("=> Processing initial node again");
        showNode(story.getInitialNode());
    }

    /**
     * Reset all the options and return to a clean state for the options panel
     */
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

    /**
     * Triggered when an option is selected
     * 
     * @param nodeOption
     *            The option selected
     * @throws BSTException
     */
    private void optionSelected(final NodeOption nodeOption) throws BSTException
    {
        int nextNode = nodeOption.getNextNode();
        for(final ActionDescriptor oa : nodeOption.getDoOnClickActions())
        {
            oa.exec();
        }
        showNode(story.getNode(nextNode));
    }

    /**
     * Log a message
     * 
     * @param message
     *            the message to log
     */
    public static void log(String message)
    {
        // TODO Add a better logging system
        System.out.println(message);
    }

    /**
     * Get the title of this story, used to get the title for the tab
     * 
     * @return
     */
    public String getTitle()
    {
        HashMap<String, String> tagMap = story.getTagMap();
        return Lang.get("story.title").replace("$t", tagMap.getOrDefault("title", Lang.get("story.missingtitle"))).replace("$a", tagMap.getOrDefault("author", Lang.get("story.missingauthor")));
    }

    /**
     * Check if there is anything to do after creation and we can continue
     * running the story
     * 
     * @return true if we can continue running the story, false otherwise
     */
    public boolean postCreation()
    {
        log("Issuing NSFW warning");
        if(story.hasTag("nsfw") && JOptionPane.showConfirmDialog(this, Lang.get("story.nsfw"), Lang.get("story.nsfw.title"), JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
        {
            log("=> Close");
            return false;
        }
        else if(nodeIdLabel != null)
            nodeIdLabel.setForeground(Color.RED);
        return true;
    }

    /**
     * Notify that the variable watcher is closing (this is used to reset the
     * button's state
     */
    protected void variableWatchClosing()
    {
        variableWatcherButton.setSelected(false);
        variableWatcher.deathNotified();
        variableWatcher.dispose();
    }

    /**
     * Get the file the story is from
     * 
     * @return the file originally put in the constructor
     */
    public File getBSTFile()
    {
        return bstFile;
    }
}
