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
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringEscapeUtils;

import com.github.rjeschke.txtmark.Processor;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTCentral;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.JScrollablePanel.ScrollableSizeHint;

public class BranchingStoryPlayerSwing extends JFrame
{
    public static final String VERSION = "0.1-RC1";
    private static final long serialVersionUID = 1L;

    private static File file;
    private static BranchingStoryTreeParser parser = new BranchingStoryTreeParser();

    private final JPanel panel = new JPanel();
    private final BranchingStory story;
    private StoryNode currentNode;
    private final NodeOption[] options;
    private final JButton[] optionsButton;
    private final JLabel textLabel;
    private Color normalButtonFg;

    public static void main(final String[] args)
    {
        log("OpenBST version " + VERSION + ", part of the BST project");
        log("[ INIT ]");
        log("Applying Look and Feel");
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            log("=> GTKLookAndFeel");
        }
        catch(final Exception e)
        {
            // Do not print as an exception is thrown in most cases
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                log("=> System LookAndFeel");
            }
            catch(final Exception e1)
            {
                e1.printStackTrace();
                log("=> No LookAndFeel compatible. Using default");
            }
        }

        log("[ FILE ]");
        log("Asking for file");
        final FileDialog jfc = new FileDialog((Dialog)null);
        jfc.setLocationRelativeTo(null);
        jfc.setTitle("Choose a Branching Story Tree file...");
        jfc.setVisible(true);
        if(jfc.getFile() != null)
        {
            log("=> File selected");
            file = new File(jfc.getDirectory() + jfc.getFile());
            log("[ LAUNCHING ]");
            loadFile();
        }
        else
        {
            log("=> No file selected. Exit.");
            System.exit(0);
        }
    }

    private static void loadFile()
    {
        try
        {
            log("Parsing story");
            BranchingStory story = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))), new Dictionnary());

            log("Creating window");
            final BranchingStoryPlayerSwing window = new BranchingStoryPlayerSwing(story);

            log("Applying BSTCentral settings");
            BSTCentral.setPlayerComponent(window);
        }
        catch(final IOException e)
        {
            log("=! IOException caught");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "<html>There was an error during file loading. Please try again and make sure your file is correct.<p>(" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        catch(final BSTException e)
        {
            log("=! BSTException caught");
            e.printStackTrace();
            String s = "<html><b>-- BST Error --</b><p>";
            s += "Your file seems to have an error here :<p>";
            s += "Line : " + e.getWhere() + "<p>";
            if(e.getCause() != null)
            {
                s += "Cause : " + e.getCause().getClass().getSimpleName() + " : " + e.getCause().getMessage() + "<p>";
            }
            s += "Message : " + e.getMessage() + "<p>";
            s += "<b>-- BST Error --</b>";
            s += "<p><p>Do you wish to reload the file?";
            if(JOptionPane.showConfirmDialog(null, s, "BST Error", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                log("Reloading");
                loadFile();
            }
            else
            {
                System.exit(0);
            }
        }
        catch(final Exception e)
        {
            log("=! Random exception caught");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "OpenBST crashed upon opening your file. Your file may have a problem.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public BranchingStoryPlayerSwing(final BranchingStory story)
    {
        log("=> Initial setup");
        this.story = story;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        updateStory();
        try
        {
            log("=> Loading icon");
            setIconImage(ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icon/icon.png")));
        }
        catch(final IOException e1)
        {
            log("=! IOException caught when loading icon");
            e1.printStackTrace();
        }

        log("=> Creating visual elements");
        getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][]"));

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.WHITE);
        getContentPane().add(scrollPane, "cell 0 0,grow");

        textLabel = new JLabel("<html>Please wait...");
        textLabel.setFont(new JTextArea().getFont());
        textLabel.setForeground(Color.BLACK);
        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        textLabel.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(final MouseEvent ev)
            {
                if(SwingUtilities.isRightMouseButton(ev))
                {
                    final JPopupMenu menu = new JPopupMenu();

                    final JMenuItem jmi = new JMenuItem("Node : " + currentNode.getId());
                    jmi.setEnabled(false);
                    menu.add(jmi);
                    menu.add(new JSeparator());

                    final JMenuItem restart = new JMenuItem("Restart from the beginning (without resetting)");
                    restart.addActionListener(ev2 ->
                    {
                        log("Showing initial node again (no reset)");
                        showNode(story.getInitialNode());
                    });
                    menu.add(restart);

                    final JMenuItem reset = new JMenuItem("Reset and restart from the beginning");
                    reset.addActionListener(ev2 ->
                    {
                        log("Resetting");
                        log("=> Performing internal reset");
                        story.reset();
                        log("Showing initial node");
                        showNode(story.getInitialNode());
                    });
                    menu.add(reset);

                    final JMenuItem reload = new JMenuItem("Reload the source file(s), reset and restart");
                    reload.addActionListener(ev2 ->
                    {
                        log("[ FULL RELOAD ]");
                        dispose();
                        loadFile();
                    });
                    menu.add(reload);

                    menu.show(textLabel, ev.getX(), ev.getY());
                }
            }
        });
        final JScrollablePanel jsp = new JScrollablePanel(new BorderLayout());
        jsp.add(textLabel, BorderLayout.CENTER);
        jsp.setScrollableWidth(ScrollableSizeHint.FIT);
        jsp.setBackground(Color.WHITE);
        scrollPane.setViewportView(jsp);
        getContentPane().add(panel, "cell 0 1,growx,aligny top");

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

        setSize(830, 480);
        setLocationRelativeTo(null);
        setVisible(true);

        log("Issuing NSFW warning");
        if(story.hasTag("nsfw") && JOptionPane.showConfirmDialog(this, "<html><b>WARNING</b><p>You are about to read a NSFW story. This story is not suitable for children.<p>Only click OK if you are OVER 18 YEARS OLD.", "NSFW WARNING", JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
        {
            log("=> Exit");
            System.exit(0);
        }
    }

    private void updateStory()
    {
        setTitle(story.getTagMap().getOrDefault("title", "<untitled>") + " by " + story.getTagMap().getOrDefault("author", "<unknown>") + " -- BST Player " + VERSION);
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
                System.exit(-1);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "The node that was supposed to be opened does not exist. Current node : " + currentNode.getId(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        log("=> Trying to show node : " + storyNode.getId());

        currentNode = storyNode;

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
                log("=> Text node detexted");
                final TextNode textNode = (TextNode)storyNode;

                log("=> Solving variables");
                String text = StoryUtils.solveVariables(textNode, story);

                log("=> Solving markup language");
                // Process the markup language
                // 0 == none
                // 1 == Markdown
                // 2 == HTML
                final int markupLanguage = solveMarkup(textNode);

                switch(markupLanguage)
                {
                case 1:
                    log("=> Processing markup language : Markdown");
                    text = "<html>" + Processor.process(text); // MD to HTML
                    // TODO Test to see if HTML characters are escaped
                    break;
                case 2:
                    log("=> Processing markup language : HTML");
                    text = "<html>" + text; // HTML to HTML
                    break;
                default:
                    log("=> Processing markup language : None");
                    text = "<html>" + StringEscapeUtils.escapeHtml(text).replace("\n", "<br>"); // Plain text to HTML
                    break;
                }

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
            }
        }
        catch(final BSTException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error on node " + storyNode.getId() + " :" + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            final ActionListener[] original = optionsButton[2].getActionListeners();
            final ActionListener[] original2 = optionsButton[3].getActionListeners();
            for(final ActionListener al : original)
            {
                optionsButton[2].removeActionListener(al);
            }
            final ActionListener shutdownListener = e -> System.exit(0);
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
            optionsButton[3].setText("Quit");
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
        log("=> Processing initial node again");
        showNode(story.getInitialNode());
    }

    private int solveMarkup(final TextNode textNode)
    {
        if(story.hasTag("markup") || textNode.hasTag("markup"))
        {
            if(textNode.hasTag("markup"))
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
        for(final ScriptAction oa : nodeOption.getDoOnClickActions())
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

}
