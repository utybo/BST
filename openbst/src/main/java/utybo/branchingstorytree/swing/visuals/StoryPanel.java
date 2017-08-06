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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.SaveState;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
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
                    new ImageIcon(OpenBST.saveAsImage))
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
                    new ImageIcon(OpenBST.undoImage))
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    if(JOptionPane.showConfirmDialog(parentWindow,
                            Lang.get("story.restoress.confirm"), Lang.get("story.restoress"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            new ImageIcon(OpenBST.undoBigImage)) == JOptionPane.YES_OPTION)
                    {
                        restoreSaveState(latestSaveState);
                    }
                }
            });
            restoreSaveStateButton.setEnabled(false);
            if(toolbarLevel > 1)
            {
                exportSaveStateButton = toolBar.add(new AbstractAction(Lang.get("story.exportss"),
                        new ImageIcon(OpenBST.exportImage))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e)
                    {
                        final FileDialog jfc = new FileDialog(parentWindow,
                                Lang.get("story.sslocation"), FileDialog.SAVE);
                        jfc.setLocationRelativeTo(parentWindow);
                        jfc.setIconImage(OpenBST.exportImage);
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
                                JOptionPane.showMessageDialog(parentWindow,
                                        Lang.get("story.exportss.error")
                                                .replace("$m", e1.getMessage())
                                                .replace("$e", e1.getClass().getSimpleName()));
                            }
                        }
                    }
                });
                exportSaveStateButton.setEnabled(false);
                toolBar.add(new AbstractAction(Lang.get("story.importss"),
                        new ImageIcon(OpenBST.importImage))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e)
                    {
                        final FileDialog jfc = new FileDialog(parentWindow,
                                Lang.get("story.sslocation"), FileDialog.LOAD);
                        jfc.setLocationRelativeTo(parentWindow);
                        jfc.setIconImage(OpenBST.importImage);
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
                                JOptionPane.showMessageDialog(parentWindow,
                                        Lang.get("story.exportss.error")
                                                .replace("$m", e1.getMessage())
                                                .replace("$e", e1.getClass().getSimpleName()));
                            }
                        }
                    }
                });
                if(toolbarLevel > 2)
                {
                    toolBar.addSeparator();
                    toolBar.add(new AbstractAction(Lang.get("story.reset"),
                            new ImageIcon(OpenBST.returnImage))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(final ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(parentWindow,
                                    Lang.get("story.reset.confirm"), Lang.get("story.reset"),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                    new ImageIcon(
                                            OpenBST.returnBigImage)) == JOptionPane.YES_OPTION)
                            {
                                reset();
                            }
                        }
                    });
                    toolBar.add(new AbstractAction(Lang.get("story.sreload"),
                            new ImageIcon(OpenBST.refreshImage))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(final ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(parentWindow,
                                    Lang.get("story.sreload.confirm"),
                                    Lang.get("story.sreload.confirm.title"),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                    new ImageIcon(
                                            OpenBST.refreshBigImage)) == JOptionPane.YES_OPTION)
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
                            new ImageIcon(OpenBST.synchronizeImage))
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(final ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(parentWindow,
                                    Lang.get("story.hreload.confirm"),
                                    Lang.get("story.hreload.confirm.title"),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                    new ImageIcon(
                                            OpenBST.synchronizeBigImage)) == JOptionPane.YES_OPTION)
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
                                new ImageIcon(OpenBST.jumpImage))
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
                                new ImageIcon(OpenBST.addonSearchImage));
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
                new ImageIcon(OpenBST.visibleImage));
        seeBackgroundButton.addActionListener(e ->
        {
            nodePanel.setBackgroundVisible(!seeBackgroundButton.isSelected());
            seeBackgroundButton
                    .setIcon(new ImageIcon(seeBackgroundButton.isSelected() ? OpenBST.invisibleImage
                            : OpenBST.visibleImage));

        });
        seeBackgroundButton.setToolTipText(Lang.get("story.backgroundvisible"));
        toolBar.add(seeBackgroundButton);

        backgroundButton = toolBar.add(new AbstractAction(Lang.get("story.seebackground"),
                new ImageIcon(OpenBST.pictureImage))
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
                dialog.setIconImage(OpenBST.pictureImage);
                dialog.setSize(1280, 720);
                dialog.setLocationRelativeTo(parentWindow);
                dialog.setVisible(true);
            }
        });

        final JToggleButton muteButton = new JToggleButton("", new ImageIcon(OpenBST.speakerImage));
        muteButton.addActionListener(e ->
        {
            final SSBClient ssb = client.getSSBHandler();
            if(ssb != null)
            {
                ssb.setMuted(muteButton.isSelected());
                muteButton.setIcon(new ImageIcon(
                        muteButton.isSelected() ? OpenBST.muteImage : OpenBST.speakerImage));
            }
        });
        muteButton.setToolTipText(Lang.get("story.mute"));
        toolBar.add(muteButton);

        toolBar.add(new AbstractAction(Lang.get("story.close"), new ImageIcon(OpenBST.closeImage))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.close.confirm"),
                        Lang.get("story.close"), JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(OpenBST.closeBigImage)) == JOptionPane.YES_OPTION)
                {
                    client.getSSBHandler().shutdown();
                    nodePanel.dispose();
                    parentWindow.removeStory(StoryPanel.this);
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
                SwingUtilities
                        .invokeLater(() -> JOptionPane.showMessageDialog(OpenBST.getInstance(),
                                Lang.get("story.modulerestorefail").replace("$m", "BRM"),
                                Lang.get("error"), JOptionPane.ERROR_MESSAGE));
            }
        }).start();;

        try
        {
            client.getUIBarHandler().restoreState();
        }
        catch(final BSTException e)
        {
            LOG.error("Error on UIB restore attempt", e);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(OpenBST.getInstance(),
                    Lang.get("story.modulerestorefail").replace("$m", "UIB"), Lang.get("error"),
                    JOptionPane.ERROR_MESSAGE));
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
                JOptionPane.showMessageDialog(OpenBST.getInstance(),
                        Lang.get("story.unknownstory").replace("$s", from), Lang.get("error"),
                        JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                StoryNode node = bs.getNode(ss.getNodeId());
                if(node == null)
                {
                    LOG.error("Unknown node (id " + ss.getNodeId() + " from " + from + ")");
                    JOptionPane.showMessageDialog(OpenBST.getInstance(),
                            Lang.get("story.missingnode").replace("$n", "" + ss.getNodeId())
                                    .replace("$a", "?").replace("$f", from),
                            Lang.get("error"), JOptionPane.ERROR_MESSAGE);
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
        LOG.trace("=> Analyzing options and deducing maximum option amount");
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
        if(maxOptions % 2 != 0)
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
                catch(final NodeNotFoundException e)
                {
                    LOG.error("Node not found : " + e.getId());
                    if(currentNode == null)
                    {
                        LOG.debug("=> It was the initial node");
                        JOptionPane.showMessageDialog(this, Lang.get("story.missinginitial"),
                                Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this,
                                Lang.get("story.missingnode").replace("$n", "" + e.getId())
                                        .replace("$f", "" + e.getSourceFile())
                                        .replace("$a", "<none>"),
                                Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                catch(final BSTException e)
                {
                    LOG.error("Encountered an error while triggering option", e);
                    JOptionPane.showMessageDialog(this,
                            Lang.get("story.error").replace("$n", "" + currentNode.getId())
                                    .replace("$a", currentNode.getTagOrDefault("alias", "<none>"))
                                    .replace("$f", e.getSourceFile()).replace("$m", e.getMessage()),
                            Lang.get("error"), JOptionPane.ERROR_MESSAGE);
                }
            });
            optionPanel.add(button);
            optionsButton[i] = button;
            button.setEnabled(false);
        }

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
            JOptionPane.showMessageDialog(OpenBST.getInstance(), Lang.get("story.nullnode"),
                    Lang.get("error"), JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(OpenBST.getInstance(),
                            Lang.get("story.logicalnodedeadend")
                                    .replace("$n", "" + storyNode.getId())
                                    .replace("$f", storyNode.getStory().getTag("__sourcename"))
                                    .replace("$a", storyNode.getTagOrDefault("alias", "<none>")));
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
            JOptionPane.showMessageDialog(this, s, Lang.get("error"), JOptionPane.ERROR_MESSAGE);
        }
        catch(final Exception e)
        {
            LOG.error("Encountered a generic exception while trying to show a node", e);
            JOptionPane.showMessageDialog(this,
                    Lang.get("story.error").replace("$n", "" + currentNode.getId())
                            .replace("$a", currentNode.getTagOrDefault("alias", "<none>"))
                            .replace("$f", storyNode.getStory().getTag("__sourcename"))
                            .replace("$m", e.getMessage() == null ? "N/A" : e.getMessage()),
                    Lang.get("error"), JOptionPane.ERROR_MESSAGE);
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
        if(validOptions.size() > 0)
        {
            LOG.trace("=> Valid options found (" + validOptions.size() + " valid on "
                    + textNode.getOptions().size() + " total)");
            LOG.trace("=> Processing options");
            for(int i = 0; i < validOptions.size(); i++)
            {
                final NodeOption option = validOptions.get(i);
                final JButton button = optionsButton[i];
                options[i] = option;
                button.setEnabled(true);
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
                button.setText(option.getText());
            }
        }
        else
        {
            LOG.trace("=> No valid options found (" + validOptions.size() + " total");
            LOG.trace("=> Showing ending");
            optionsButton[0].setText(Lang.get("story.final.end"));
            optionsButton[1]
                    .setText(Lang.get("story.final.node").replace("$n", "" + textNode.getId()));
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
                    LOG.trace("Resetting story");
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
            if(JOptionPane.showConfirmDialog(parentWindow, Lang.get("story.nsfw"),
                    Lang.get("story.nsfw.title"),
                    JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
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
