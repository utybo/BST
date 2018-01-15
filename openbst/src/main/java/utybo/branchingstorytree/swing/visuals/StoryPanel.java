/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.visuals;

import static utybo.branchingstorytree.swing.OpenBST.LOG;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.SaveState;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.Messagers;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.impl.SSBClient;
import utybo.branchingstorytree.swing.impl.TabClient;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.visuals.JScrollablePanel.ScrollableSizeHint;

public class StoryPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
    private final TabClient client;

    /**
     * The latest save state made - this is used with the save state buttons
     */
    private SaveState latestSaveState;

    /**
     * The file this story is from
     */
    private final File bstFile;

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
     * The panel in which option buttons are placed
     */
    private final JPanel optionPanel = new JPanel();

    // --- Toolbar buttons ---
    private JButton restoreSaveStateButton, exportSaveStateButton;
    protected JToggleButton variableWatcherButton;
    protected VariableWatchDialog variableWatcher;
    private JButton backgroundButton;
    private JButton jsHint, hrefHint;

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
    public StoryPanel(final BranchingStory story, final OpenBST parentWindow, final File f,
            final TabClient client)
    {
        LOG.trace("=> Initial setup");
        bstFile = f;
        client.setStoryPanel(this);
        this.story = story;
        this.parentWindow = parentWindow;
        this.client = client;

        LOG.trace("=> Creating visual elements");
        setLayout(new MigLayout("hidemode 3", "[grow]", ""));

        createToolbar();

        if(story.hasTag("uib_layout"))
        {
            uibPanel = new JPanel();
            add(uibPanel, "growx, wrap");
            uibPanel.setVisible(false);
        }

        nodePanel = new NodePanel(story, this, client.getIMGHandler());
        client.setNodePanel(nodePanel);
        nodePanel.setScrollableWidth(ScrollableSizeHint.FIT);
        nodePanel.setScrollableHeight(ScrollableSizeHint.STRETCH);
        //        scrollPane.setViewportView(nodePanel);
        add(nodePanel, "grow, pushy, wrap");

        add(optionPanel, "growx");
    }

    /**
     * Create the toolbar, enforcing the supertools tag
     */
    private void createToolbar()
    {
        final int toolbarLevel = readToolbarLevel();
        final JToolBar toolBar = new JToolBar();

        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        if(toolbarLevel > 0)
        {
            toolBar.add(new AbstractAction(Lang.get("story.createss"),
                    new ImageIcon(Icons.getImage("Save As", 16)))
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    latestSaveState = new SaveState(currentNode.getId(), story.getRegistry(),
                            currentNode.getStory().getTag("__sourcename"));
                    restoreSaveStateButton.setEnabled(true);
                    if(exportSaveStateButton != null)
                    {
                        exportSaveStateButton.setEnabled(true);
                    }
                }
            });
            restoreSaveStateButton = toolBar.add(new AbstractAction(Lang.get("story.restoress"),
                    new ImageIcon(Icons.getImage("Undo", 16)))
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    if(Messagers.showConfirm(parentWindow,
                            "<html><body style='width: " + (int)(Icons.getScale() * 300) + "'>"
                                    + Lang.get("story.restoress.confirm"),
                            Messagers.TYPE_WARNING, Messagers.OPTIONS_YES_NO,
                            Lang.get("story.restoress"),
                            new ImageIcon(Icons.getImage("Undo", 40))) == Messagers.OPTION_YES)
                    {
                        restoreSaveState(latestSaveState);
                    }
                }
            });
            restoreSaveStateButton.setEnabled(false);
            if(toolbarLevel > 1)
            {
                exportSaveStateButton = toolBar.add(new AbstractAction(Lang.get("story.exportss"),
                        new ImageIcon(Icons.getImage("Export", 16)))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e)
                    {
                        final FileDialog jfc = new FileDialog(parentWindow,
                                Lang.get("story.sslocation"), FileDialog.SAVE);
                        jfc.setLocationRelativeTo(parentWindow);
                        jfc.setIconImage(Icons.getImage("Export", 16));
                        jfc.setVisible(true);
                        if(jfc.getFile() != null)
                        {
                            final File file = new File(jfc.getFile().endsWith(".bss")
                                    ? jfc.getDirectory() + jfc.getFile()
                                    : jfc.getDirectory() + jfc.getFile() + ".bss");
                            final Gson gson = new Gson();
                            if(file.exists())
                            {
                                if(!file.delete())
                                {
                                    LOG.warn("Failed to delete file");
                                }
                            }
                            try
                            {
                                if(!file.createNewFile())
                                {
                                    LOG.warn("Failed to create file");
                                }
                                try(OutputStreamWriter writer = new OutputStreamWriter(
                                        new FileOutputStream(file), StandardCharsets.UTF_8);)
                                {
                                    gson.toJson(
                                            new SaveState(currentNode.getId(), story.getRegistry(),
                                                    currentNode.getStory().getTag("__sourcename")),
                                            writer);
                                    writer.flush();
                                }
                            }
                            catch(final IOException e1)
                            {
                                LOG.error("Had an IOException while exporting Save State", e1);
                                Messagers.showException(parentWindow,
                                        Lang.get("story.exportss.error")
                                                .replace("$m", e1.getMessage())
                                                .replace("$e", e1.getClass().getSimpleName()),
                                        e1);
                            }
                        }
                    }
                });
                exportSaveStateButton.setEnabled(false);
                toolBar.add(new AbstractAction(Lang.get("story.importss"),
                        new ImageIcon(Icons.getImage("Import", 16)))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e)
                    {
                        final FileDialog jfc = new FileDialog(parentWindow,
                                Lang.get("story.sslocation"), FileDialog.LOAD);
                        jfc.setLocationRelativeTo(parentWindow);
                        jfc.setIconImage(Icons.getImage("Import", 16));
                        jfc.setVisible(true);
                        if(jfc.getFile() != null)
                        {
                            final File file = new File(jfc.getDirectory() + jfc.getFile());
                            final Gson gson = new Gson();
                            try
                            {
                                final InputStreamReader reader = new InputStreamReader(
                                        new FileInputStream(file), StandardCharsets.UTF_8);
                                latestSaveState = gson.fromJson(reader, SaveState.class);
                                reader.close();
                                restoreSaveState(latestSaveState);
                            }
                            catch(final IOException e1)
                            {
                                LOG.error("Had an IOException while importing Save State", e1);
                                Messagers.showException(parentWindow,
                                        Lang.get("story.importss.error")
                                                .replace("$m", e1.getMessage())
                                                .replace("$e", e1.getClass().getSimpleName()),
                                        e1);
                            }
                        }
                    }
                });
                if(toolbarLevel > 2)
                {
                    toolBar.addSeparator();
                    toolBar.add(new AbstractAction(Lang.get("story.reset"),
                            new ImageIcon(Icons.getImage("Return", 16)))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(final ActionEvent e)
                        {
                            if(Messagers.showConfirm(parentWindow,
                                    "<html>" + Lang.get("story.reset.confirm"),
                                    Messagers.OPTIONS_YES_NO, Messagers.TYPE_WARNING,
                                    Lang.get("story.reset"), new ImageIcon(
                                            Icons.getImage("Return", 40))) == Messagers.OPTION_YES)
                            {
                                reset();
                            }
                        }
                    });
                    toolBar.add(new AbstractAction(Lang.get("story.sreload"),
                            new ImageIcon(Icons.getImage("Refresh", 16)))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(final ActionEvent e)
                        {
                            if(Messagers.showConfirm(parentWindow,
                                    "<html><body style='width: " + (int)(Icons.getScale() * 300)
                                            + "'>" + Lang.get("story.sreload.confirm"),
                                    Messagers.OPTIONS_YES_NO, Messagers.TYPE_WARNING,
                                    Lang.get("story.sreload.confirm.title"), new ImageIcon(
                                            Icons.getImage("Refresh", 40))) == Messagers.OPTION_YES)
                            {

                                final SaveState ss = new SaveState(currentNode.getId(),
                                        story.getRegistry(),
                                        currentNode.getStory().getTag("__sourcename"));
                                reload(o ->
                                {
                                    restoreSaveState(ss);
                                });
                            }
                        }
                    });
                    toolBar.add(new AbstractAction(Lang.get("story.hreload"),
                            new ImageIcon(Icons.getImage("Synchronize", 16)))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(final ActionEvent e)
                        {
                            if(Messagers.showConfirm(parentWindow,
                                    "<html>" + Lang.get("story.hreload.confirm"),
                                    Messagers.OPTIONS_YES_NO, Messagers.TYPE_WARNING,
                                    Lang.get("story.hreload.confirm.title"), new ImageIcon(Icons
                                            .getImage("Synchronize", 40))) == Messagers.OPTION_YES)
                            {
                                reset();
                                reload(o ->
                                {});
                            }
                        }
                    });
                    if(toolbarLevel > 3)
                    {
                        toolBar.addSeparator();
                        toolBar.add(new AbstractAction(Lang.get("story.jumptonode"),
                                new ImageIcon(Icons.getImage("Easy to Find", 16)))
                        {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void actionPerformed(final ActionEvent e)
                            {
                                new JumpToNodeDialog(client, story, n -> showNode(n))
                                        .setVisible(true);;
                            }
                        });
                        variableWatcherButton = new JToggleButton("",
                                new ImageIcon(Icons.getImage("Camera Addon Identification", 16)));
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
        }

        toolBar.add(Box.createHorizontalGlue());

        toolBar.addSeparator();

        hrefHint = new JButton("");
        hrefHint.setEnabled(false);
        hrefHint.setVisible(false);
        toolBar.add(hrefHint);

        jsHint = new JButton("");
        jsHint.setEnabled(false);
        jsHint.setVisible(false);
        toolBar.add(jsHint);

        final JToggleButton seeBackgroundButton = new JToggleButton("",
                new ImageIcon(Icons.getImage("Eye", 16)));
        seeBackgroundButton.addActionListener(e ->
        {
            nodePanel.setBackgroundVisible(!seeBackgroundButton.isSelected());
            seeBackgroundButton.setIcon(
                    new ImageIcon(seeBackgroundButton.isSelected() ? Icons.getImage("Invisible", 16)
                            : Icons.getImage("Eye", 16)));

        });
        seeBackgroundButton.setToolTipText(Lang.get("story.backgroundvisible"));
        toolBar.add(seeBackgroundButton);

        backgroundButton = toolBar.add(new AbstractAction(Lang.get("story.seebackground"),
                new ImageIcon(Icons.getImage("Picture", 16)))
        {

            /**
             *
             */
            private static final long serialVersionUID = 1L;
            private Dimension previousBounds;
            private Image previousImage;
            private int x, y;

            @Override
            public void actionPerformed(final ActionEvent e)
            {
                final JDialog dialog = new JDialog(parentWindow);
                dialog.getContentPane().add(new JPanel()
                {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void paintComponent(final Graphics g)
                    {
                        super.paintComponent(g);
                        Image image;
                        final int width = getWidth() - 1;
                        final int height = getHeight() - 1;
                        if(previousBounds != null && previousImage != null
                                && getParent().getSize().equals(previousBounds))
                        {
                            image = previousImage;
                        }
                        else
                        {
                            final BufferedImage bi = client.getIMGHandler().getCurrentBackground();
                            double scaleFactor = 1d;
                            if(bi.getWidth() > bi.getHeight())
                            {
                                scaleFactor = getScaleFactorToFit(
                                        new Dimension(bi.getWidth(), bi.getHeight()),
                                        getParent().getSize());
                            }
                            else if(bi.getHeight() > bi.getWidth())
                            {
                                scaleFactor = getScaleFactorToFit(
                                        new Dimension(bi.getWidth(), bi.getHeight()),
                                        getParent().getSize());
                            }
                            final int scaleWidth = (int)Math.round(bi.getWidth() * scaleFactor);
                            final int scaleHeight = (int)Math.round(bi.getHeight() * scaleFactor);

                            image = bi.getScaledInstance(scaleWidth, scaleHeight,
                                    Image.SCALE_SMOOTH);

                            previousBounds = getParent().getSize();
                            previousImage = image;
                            x = (width - image.getWidth(this)) / 2;
                            y = (height - image.getHeight(this)) / 2;
                        }

                        g.drawImage(image, x, y, this);
                    }

                    private double getScaleFactorToFit(final Dimension masterSize,
                            final Dimension targetSize)
                    {
                        final double dScaleWidth = getScaleFactor(masterSize.width,
                                targetSize.width);
                        final double dScaleHeight = getScaleFactor(masterSize.height,
                                targetSize.height);
                        final double dScale = Math.min(dScaleHeight, dScaleWidth);
                        return dScale;
                    }

                    private double getScaleFactor(final int iMasterSize, final int iTargetSize)
                    {
                        double dScale = 1;
                        dScale = (double)iTargetSize / (double)iMasterSize;
                        return dScale;
                    }
                });
                dialog.setTitle(Lang.get("story.background"));
                dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                dialog.setIconImage(Icons.getImage("Picture", 16));
                dialog.setSize((int)(Icons.getScale() * 1280), (int)(Icons.getScale() * 720));
                dialog.setLocationRelativeTo(parentWindow);
                dialog.setVisible(true);
            }
        });

        final JToggleButton muteButton = new JToggleButton("",
                new ImageIcon(Icons.getImage("Audio", 16)));
        muteButton.addActionListener(e ->
        {
            final SSBClient ssb = client.getSSBHandler();
            if(ssb != null)
            {
                ssb.setMuted(muteButton.isSelected());
                muteButton
                        .setIcon(new ImageIcon(muteButton.isSelected() ? Icons.getImage("Mute", 16)
                                : Icons.getImage("Audio", 16)));
            }
        });
        muteButton.setToolTipText(Lang.get("story.mute"));
        toolBar.add(muteButton);

        toolBar.add(new AbstractAction(Lang.get("story.close"),
                new ImageIcon(Icons.getImage("Cancel", 16)))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e)
            {
                if(Messagers.showConfirm(parentWindow, "<html>" + Lang.get("story.close.confirm"),
                        Messagers.OPTIONS_YES_NO, Messagers.TYPE_WARNING, Lang.get("story.close"),
                        new ImageIcon(Icons.getImage("Cancel", 40))) == Messagers.OPTION_YES)
                {
                    client.getSSBHandler().shutdown();
                    nodePanel.dispose();
                    parentWindow.removeTab(StoryPanel.this);
                }
            }
        });

        for(final Component component : toolBar.getComponents())
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
        final String value = story.getTag("supertools");
        if(value == null)
        {
            return 4;
        }
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
    protected void restoreSaveState(final SaveState ss)
    {
        OpenBST.LOG.trace("Restoring from " + ss.getFrom() + " id " + ss.getNodeId());

        ss.applySaveState(story);

        // Also notify modules that need to restore their state
        client.getSSBHandler().restoreSaveState();
        client.getIMGHandler().restoreSaveState();
        new Thread(() ->
        {
            try
            {
                // BRM needs to be reset on anything but the EDT, thus we need
                // to launch it in a separate thread.
                // Fear not, its progress monitor blocks interaction with the application
                client.getBRMHandler().restoreSaveState();
            }
            catch(final BSTException e)
            {
                LOG.error("Error on BRM restore attempt", e);
                SwingUtilities.invokeLater(() -> Messagers.showException(OpenBST.getInstance(),
                        Lang.get("story.modulerestorefail").replace("$m", "BRM"), e));
            }
        }).start();;

        try
        {
            client.getUIBarHandler().restoreState();
        }
        catch(final BSTException e)
        {
            LOG.error("Error on UIB restore attempt", e);
            SwingUtilities.invokeLater(() -> Messagers.showException(OpenBST.getInstance(),
                    Lang.get("story.modulerestorefail").replace("$m", "UIB"), e));
        }
        String from = ss.getFrom();
        if(from == null || "<main>".equals(from))
        {
            showNode(story.getNode(ss.getNodeId()));
        }
        else
        {
            BranchingStory bs = client.getXBFHandler().getAdditionalStory(from);
            if(bs == null)
            {
                LOG.error("Unknown story : " + from);
                Messagers.showMessage(OpenBST.getInstance(),
                        Lang.get("story.unknownstory").replace("$s", from), Messagers.TYPE_ERROR);
            }
            else
            {
                StoryNode node = bs.getNode(ss.getNodeId());
                if(node == null)
                {
                    LOG.error("Unknown node (id " + ss.getNodeId() + " from " + from + ")");
                    Messagers
                            .showMessage(OpenBST.getInstance(),
                                    Lang.get("story.missingnode").replace("$n", "" + ss.getNodeId())
                                            .replace("$a", "?").replace("$f", from),
                                    Messagers.TYPE_ERROR);
                }
                else
                {
                    showNode(node);
                }
            }
        }
    }

    /**
     * Setup of the story. This can be ran again if the story changed
     */
    public void setupStory()
    {
        LOG.trace("Displaying first node");
        showNode(story.getInitialNode());
    }

    /**
     * Reload the file (not clean, this is a subroutine method part of the
     * entire reloading process)
     */
    protected void reload(Consumer<BranchingStory> callback)
    {
        parentWindow.loadFile(bstFile, client, bs ->
        {
            story = bs;
            try
            {
                client.getBRMHandler().load();
            }
            catch(BSTException e)
            {
                OpenBST.LOG.error("Failed to reload resources", e);
            }
            try
            {
                SwingUtilities.invokeAndWait(() ->
                {
                    setupStory();
                    callback.accept(bs);
                });
            }
            catch(Exception e)
            {
                OpenBST.LOG.error("Failed to setup story", e);
            }
        });
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
            // This should never happen

            LOG.error("Tried to show a null node!");
            Messagers.showMessage(OpenBST.getInstance(), Lang.get("story.nullnode"),
                    Messagers.TYPE_ERROR);
            return;
        }

        LOG.trace("=> Trying to show node : " + storyNode.getId());

        currentNode = storyNode;
        if(nodeIdLabel != null)
        {
            nodeIdLabel
                    .setText("Node : " + (currentNode.getId() > 0 ? currentNode.getId() : "[auto]")
                            + (currentNode.hasTag("alias")
                                    ? " (" + currentNode.getTag("alias") + ")"
                                    : ""));
        }

        try
        {
            // If this is a LogicalNode, we need to solve it.
            if(storyNode instanceof LogicalNode)
            {
                LOG.trace("=> Solving logical node");
                StoryNode node = ((LogicalNode)storyNode).solve(story);
                LOG.trace("=> Logical node result : " + (node == null ? "null" : node.getId()));
                if(node == null)
                {
                    Messagers.showMessage(OpenBST.getInstance(),
                            Lang.get("story.logicalnodedeadend")
                                    .replace("$n", "" + storyNode.getId())
                                    .replace("$f", storyNode.getStory().getTag("__sourcename"))
                                    .replace("$a", storyNode.getTagOrDefault("alias", "<none>")),
                            Messagers.TYPE_ERROR);
                }
                else
                {
                    showNode(node);
                }
            }

            // This is supposed to be executed when the StoryNode is a TextNode
            if(storyNode instanceof TextNode)
            {
                LOG.trace("=> Text node detected");
                final TextNode textNode = (TextNode)storyNode;

                LOG.trace("=> Applying text");
                nodePanel.applyNode(textNode.getStory(), textNode);

                LOG.trace("Resetting options");
                resetOptions();

                LOG.trace("Applying options for node : " + textNode.getId());
                showOptions(textNode);

                LOG.trace("Updating UIB if necessary");
                client.getUIBarHandler().updateUIB();

                backgroundButton.setEnabled(client.getIMGHandler().getCurrentBackground() != null);
            }
        }
        catch(final BSTException e)
        {
            LOG.error("Encountered a BST exception while trying to show a node", e);
            final String s = Lang.get("story.error2").replace("$n", "" + currentNode.getId())
                    .replace("$a", currentNode.getTagOrDefault("alias", "<none>"))
                    .replace("$m", e.getMessage()).replace("$l", e.getWhere() + "");
            Messagers.showException(OpenBST.getInstance(), s, e);
        }
        catch(final Exception e)
        {
            LOG.error("Encountered a generic exception while trying to show a node", e);
            Messagers.showException(OpenBST.getInstance(),
                    Lang.get("story.error").replace("$n", "" + currentNode.getId())
                            .replace("$a", currentNode.getTagOrDefault("alias", "<none>"))
                            .replace("$f", storyNode.getStory().getTag("__sourcename"))
                            .replace("$m", e.getMessage() == null ? "N/A" : e.getMessage()),
                    e);
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
        LOG.trace("=> Filtering valid options");
        final ArrayList<NodeOption> validOptions = new ArrayList<>();
        for(final NodeOption no : textNode.getOptions())
        {
            if(no.getChecker().check())
            {
                validOptions.add(no);
            }
        }
        optionPanel.removeAll();
        optionPanel.setLayout(new MigLayout("wrap 2, fill", "[50%][50%]", ""));
        if(validOptions.size() > 0)
        {
            LOG.trace("=> Valid options found (" + validOptions.size() + " valid on "
                    + textNode.getOptions().size() + " total)");
            LOG.trace("=> Processing options");
            for(int i = 0; i < validOptions.size(); i++)
            {
                final NodeOption option = validOptions.get(i);
                final JButton button = new JButton(
                        StoryUtils.solveVariables(option.getText(), story));
                button.addActionListener(ev ->
                {
                    try
                    {
                        optionSelected(option);
                    }
                    catch(final NodeNotFoundException e)
                    {
                        LOG.error("Node not found : " + e.getId());
                        if(currentNode == null)
                        {
                            LOG.debug("=> It was the initial node");
                            Messagers.showMessage(OpenBST.getInstance(),
                                    Lang.get("story.missinginitial"), Messagers.TYPE_ERROR);
                            return;
                        }
                        else
                        {
                            Messagers.showMessage(OpenBST.getInstance(),
                                    Lang.get("story.missingnode").replace("$n", "" + e.getId())
                                            .replace("$f", "" + e.getSourceFile())
                                            .replace("$a", "<none>"),
                                    Messagers.TYPE_ERROR);
                            return;
                        }
                    }
                    catch(final BSTException e)
                    {
                        LOG.error("Encountered an error while triggering option", e);
                        Messagers.showMessage(OpenBST.getInstance(), Lang.get("story.error")
                                .replace("$n", "" + currentNode.getId())
                                .replace("$a", currentNode.getTagOrDefault("alias", "<none>"))
                                .replace("$f", e.getSourceFile()).replace("$m", e.getMessage()),
                                Messagers.TYPE_ERROR);
                    }
                });
                optionPanel.add(button,
                        "grow" + (validOptions.size() % 2 == 1 && i == validOptions.size() - 1
                                ? ",spanx 2"
                                : ""));
                if(i == 0)
                {
                    button.requestFocus();
                }
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
                        catch(IllegalArgumentException | IllegalAccessException
                                | NoSuchFieldException | SecurityException e)
                        {
                            LOG.warn("Color does not exist : " + color, e);
                        }
                    }
                    if(c != null)
                    {
                        button.setForeground(c);
                    }
                }
            }
        }
        else
        {
            LOG.trace("=> No valid options found (" + validOptions.size() + " total");
            LOG.trace("=> Showing ending");
            JButton button = new JButton(Lang.get("story.final.end"));
            button.setEnabled(false);
            optionPanel.add(button, "grow");

            // TODO Make compatible with alias system
            button = new JButton(Lang.get("story.final.node").replace("$n", "" + textNode.getId()));
            button.setEnabled(false);
            optionPanel.add(button, "grow");

            button = new JButton(Lang.get("story.final.restart"));
            button.requestFocus();
            button.addActionListener(e -> reset());
            optionPanel.add(button, "grow");

            button = new JButton(Lang.get("story.final.close"));
            button.addActionListener(e -> parentWindow.removeTab(this));
            optionPanel.add(button, "grow");
        }

    }

    /**
     * Internal reset (subroutine method)
     */
    private void reset()
    {
        LOG.trace("=> Performing internal reset");
        story.reset();

        client.getUIBarHandler().resetUib();
        client.getIMGHandler().reset();
        client.getSSBHandler().reset();
        client.getBDFHandler().reset();

        LOG.trace("=> Processing initial node again");
        showNode(story.getInitialNode());
    }

    /**
     * Reset all the options and return to a clean state for the options panel
     */
    private void resetOptions()
    {
        optionPanel.removeAll();
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
        final StoryNode nextNode = nodeOption.getNextNode(currentNode.getStory());
        for(final ActionDescriptor oa : nodeOption.getDoOnClickActions())
        {
            oa.exec();
        }
        showNode(nextNode);
    }

    /**
     * Get the title of this story, used to get the title for the tab
     *
     * @return
     */
    public String getTitle()
    {
        final HashMap<String, String> tagMap = story.getTagMap();
        return Lang.get("story.title")
                .replace("$t", tagMap.getOrDefault("title", Lang.get("story.missingtitle")))
                .replace("$a", tagMap.getOrDefault("author", Lang.get("story.missingauthor")));
    }

    /**
     * Check if there is anything to do after creation and we can continue
     * running the story
     *
     * @return true if we can continue running the story, false otherwise
     */
    public boolean postCreation()
    {
        LOG.trace("NSFW warning check");
        if(story.hasTag("nsfw"))
        {
            if(Messagers.showConfirm(parentWindow, Lang.get("story.nsfw"), Messagers.OPTIONS_YES_NO,
                    Messagers.TYPE_WARNING, Lang.get("story.nsfw.title")) != Messagers.OPTION_OK)
            {
                LOG.trace("=> Close");
                return false;
            }
            else if(nodeIdLabel != null)
            {
                nodeIdLabel.setEnabled(true);
                nodeIdLabel.setForeground(Color.RED);
            }
        }
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

    public BranchingStory getStory()
    {
        return story;
    }

    public JPanel getUIBPanel()
    {
        return uibPanel;
    }

    public JButton getJSHint()
    {
        return jsHint;
    }

    public JButton getHrefHint()
    {
        return hrefHint;
    }
}
