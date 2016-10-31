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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringEscapeUtils;

import com.github.rjeschke.txtmark.Processor;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTCentral;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;
import utybo.branchingstorytree.swing.JScrollablePanel.ScrollableSizeHint;

@SuppressWarnings("serial")
public class BranchingStoryPlayerSwing extends JFrame
{
    private final JPanel panel = new JPanel();
    private static File file;

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
        catch(Exception e)
        {
            // Do not print as an exception is thrown in most cases
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch(ClassNotFoundException e1)
            {
                e1.printStackTrace();
            }
            catch(InstantiationException e1)
            {
                e1.printStackTrace();
            }
            catch(IllegalAccessException e1)
            {
                e1.printStackTrace();
            }
            catch(UnsupportedLookAndFeelException e1)
            {
                e1.printStackTrace();
            }
        }

        FileDialog jfc = new FileDialog((Dialog)null);
        jfc.setTitle("Choose a Branching Story Tree file...");
        jfc.setVisible(true);
        if(jfc.getFile() != null)
        {
            try
            {
                file = new File(jfc.getDirectory() + jfc.getFile());
                BranchingStoryPlayerSwing window = new BranchingStoryPlayerSwing(BranchingStoryTreeParser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))), new Dictionnary()));

                BSTCentral.setPlayerComponent(window);
            }
            catch(IOException e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "<html>There was an error during file loading. Please try again and make sure your file is correct.<p>(" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            catch(BSTException e)
            {
                e.printStackTrace();
                String s = "<html><b>-- BST Error --</b><p>";
                s += "Your file seems to have an error here :<p>";
                s += "Line : " + e.getWhere() + "<p>";
                if(e.getCause() != null)
                    s += "Cause : " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage() + "<p>";
                s += "Message : " + e.getMessage() + "<p>";
                s += "<b>-- BST Error --</b>";
                JOptionPane.showMessageDialog(null, s, "BST Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            System.exit(0);
        }
    }

    private BranchingStory story;
    private StoryNode currentNode;
    private NodeOption[] options;
    private JButton[] optionsButton;
    private JLabel textLabel;
    private Color normalButtonFg;

    public BranchingStoryPlayerSwing(BranchingStory story)
    {
        this.story = story;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        updateStory();
        try
        {
            setIconImage(ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icon/icon.png")));
        }
        catch(IOException e1)
        {
            e1.printStackTrace();
        }
        getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][]"));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.WHITE);
        getContentPane().add(scrollPane, "cell 0 0,grow");

        textLabel = new JLabel("<html>Please wait...");
        textLabel.setFont(new JTextArea().getFont());
        textLabel.setForeground(Color.BLACK);
        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        textLabel.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent ev)
            {
                if(SwingUtilities.isRightMouseButton(ev))
                {
                    JPopupMenu menu = new JPopupMenu();

                    JMenuItem jmi = new JMenuItem("Node : " + currentNode.getId());
                    jmi.setEnabled(false);
                    menu.add(jmi);
                    menu.add(new JSeparator());

                    JMenuItem restart = new JMenuItem("Restart from the beginning (without resetting)");
                    restart.addActionListener(ev2 -> showNode(story.getInitialNode()));
                    menu.add(restart);

                    JMenuItem reset = new JMenuItem("Reset and restart from the beginning");
                    reset.addActionListener(ev2 ->
                    {
                        story.reset();
                        showNode(story.getInitialNode());
                    });
                    menu.add(reset);

                    JMenuItem reload = new JMenuItem("Reload the source file(s), reset and restart");
                    reload.addActionListener(ev2 ->
                    {
                        try
                        {
                            dispose();
                            BranchingStoryPlayerSwing window = new BranchingStoryPlayerSwing(BranchingStoryTreeParser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))), new Dictionnary()));
                            BSTCentral.setPlayerComponent(window);
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "<html>There was an error during file loading. Please try again and make sure your file is correct.<p>(" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                        catch(BSTException e)
                        {
                            e.printStackTrace();
                            String s = "<html><b>-- BST Error --</b><p>";
                            s += "Your file seems to have an error here :<p>";
                            s += "Line : " + e.getWhere() + "<p>";
                            if(e.getCause() != null)
                                s += "Cause : " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage() + "<p>";
                            s += "Message : " + e.getMessage() + "<p>";
                            s += "<b>-- BST Error --</b>";
                            JOptionPane.showMessageDialog(null, s, "BST Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    menu.add(reload);

                    menu.show(textLabel, ev.getX(), ev.getY());
                }
            }
        });
        JScrollablePanel jsp = new JScrollablePanel(new BorderLayout());
        jsp.add(textLabel, BorderLayout.CENTER);
        jsp.setScrollableWidth(ScrollableSizeHint.FIT);
        jsp.setBackground(Color.WHITE);
        scrollPane.setViewportView(jsp);
        getContentPane().add(panel, "cell 0 1,growx,aligny top");

        // Quick analysis of all the nodes to get the maximum amount of options
        int maxOptions = 0;
        for(StoryNode sn : story.getAllNodes())
        {
            if(sn instanceof TextNode)
                if(((TextNode)sn).getOptions().size() > maxOptions)
                    maxOptions = ((TextNode)sn).getOptions().size();
        }
        if(maxOptions < 4)
            maxOptions = 4;
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
            int optionId = i;
            JButton button = new JButton();
            normalButtonFg = button.getForeground();
            button.addActionListener(ev ->
            {
                try
                {
                    optionSelected(options[optionId]);
                }
                catch(BSTException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error on node " + currentNode.getId() + " :" + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                }
            });
            panel.add(button);
            optionsButton[i] = button;
            button.setEnabled(false);
        }

        showNode(story.getInitialNode());

        setSize(830, 480);
        setLocationRelativeTo(null);
        setVisible(true);

        if(story.hasTag("nsfw"))
        {
            if(JOptionPane.showConfirmDialog(this, "<html><b>WARNING</b><p>You are about to read a NSFW story. This story is not suitable for children.<p>Only click OK if you are OVER 18 YEARS OLD.", "NSFW WARNING", JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
                System.exit(0);
        }
    }

    private void updateStory()
    {
        setTitle(story.getTagMap().getOrDefault("title", "<untitled>") + " by " + story.getTagMap().getOrDefault("author", "<unknown>") + " -- BST Player");
    }

    private void showNode(StoryNode storyNode)
    {
        currentNode = storyNode;
        try
        {
            // If this is a LogicalNode, we need to solve it.
            if(storyNode instanceof LogicalNode)
            {
                int i = ((LogicalNode)storyNode).solve();
                // TODO Throw a nicer exception when an invalid value is returned
                showNode(story.getNode(i));
            }

            // This is supposed to be executed when the StoryNode is a TextNode
            if(storyNode instanceof TextNode)
            {
                TextNode textNode = (TextNode)storyNode;

                System.out.println(textNode.getText());
                String text = textNode.getText();
                Pattern vp = Pattern.compile("\\$\\{((\\>\\d+)|(\\w+))\\}");
                Matcher vn = vp.matcher(text);
                while(vn.find())
                {
                    String toReplace = vn.group();
                    String varName = toReplace.substring(2, toReplace.length() - 1);
                    if(varName.startsWith(">"))
                    {
                        String s = varName.substring(1);
                        int i = Integer.parseInt(s);
                        text = text.replace(toReplace, ((VirtualNode)story.getNode(i)).getText());
                    }
                    else if(varName.startsWith("&"))
                    {
                        String s = varName.substring(1);
                        int i = Integer.parseInt(s);
                        LogicalNode ln = (LogicalNode)story.getNode(i);
                        text = text.replace(toReplace, ((VirtualNode)story.getNode(ln.solve())).getText());
                    }
                    else
                        text = vn.replaceFirst(story.getRegistry().get(varName).toString());
                    vn.reset(text);
                }

                // Process the markup language
                // 0 == none
                // 1 == Markdown
                // 2 == HTML
                int markupLanguage = 0;
                if(story.hasTag("markup") || textNode.hasTag("markup"))
                {
                    if(textNode.hasTag("markup"))
                    {
                        String s = textNode.getTag("markup");
                        if(s.equalsIgnoreCase("md") || s.equalsIgnoreCase("markdown"))
                        {
                            markupLanguage = 1;
                        }
                        else if(s.equalsIgnoreCase("html"))
                        {
                            markupLanguage = 2;
                        }
                    }
                    else if(story.hasTag("markup"))
                    {
                        String s = story.getTag("markup");
                        if(s.equalsIgnoreCase("md") || s.equalsIgnoreCase("markdown"))
                        {
                            markupLanguage = 1;
                        }
                        else if(s.equalsIgnoreCase("html"))
                        {
                            markupLanguage = 2;
                        }
                    }
                }

                switch(markupLanguage)
                {
                case 1:
                    text = "<html>" + Processor.process(text); // MD to HTML
                    // TODO Test to see if HTML characters are escaped
                    break;
                case 2:
                    text = "<html>" + text; // HTML to HTML
                    break;
                default:
                    text = "<html>" + StringEscapeUtils.escapeHtml(text).replace("\n", "<br>"); // Plain text to HTML
                    break;
                }

                textLabel.setText(text);
                if(textNode.hasTag("color"))
                {
                    String color = textNode.getTag("color");
                    Color c = null;
                    if(color.startsWith("#"))
                        c = new Color(Integer.parseInt(color.substring(1), 16));
                    else
                        try
                        {
                            c = (Color)Color.class.getField(color).get(null);
                        }
                        catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
                        {
                            System.err.println("COLOR DOES NOT EXIST : " + color);
                            e.printStackTrace();
                        }
                    if(c != null)
                    {
                        textLabel.setForeground(c);
                    }
                    else
                    {
                        textLabel.setForeground(Color.BLACK);
                    }
                }
                else
                {
                    textLabel.setForeground(Color.BLACK);
                }
                ArrayList<NodeOption> validOptions = new ArrayList<>();
                for(NodeOption no : textNode.getOptions())
                {
                    if(no.getChecker().check())
                    {
                        validOptions.add(no);
                    }
                }
                resetOptions();
                boolean end = true;
                for(int i = 0; i < validOptions.size(); i++)
                {
                    NodeOption option = validOptions.get(i);
                    end = false;
                    JButton button = optionsButton[i];
                    options[i] = option;
                    button.setEnabled(true);
                    if(option.hasTag("color"))
                    {
                        String color = option.getTag("color");
                        Color c = null;
                        if(color.startsWith("#"))
                            c = new Color(Integer.parseInt(color.substring(1), 16));
                        else
                            try
                            {
                                c = (Color)Color.class.getField(color).get(null);
                            }
                            catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
                            {
                                System.err.println("COLOR DOES NOT EXIST : " + color);
                                e.printStackTrace();
                            }
                        if(c != null)
                        {
                            button.setForeground(c);
                        }
                    }
                    button.setText(option.getText());
                }
                if(end)
                {
                    optionsButton[0].setText("The End.");
                    optionsButton[1].setText("Final node : " + textNode.getId());
                    optionsButton[2].setText("Restart");
                    optionsButton[2].setEnabled(true);
                    ActionListener[] original = optionsButton[2].getActionListeners();
                    ActionListener[] original2 = optionsButton[3].getActionListeners();
                    for(ActionListener al : original)
                    {
                        optionsButton[2].removeActionListener(al);
                    }
                    ActionListener shutdownListener = e -> System.exit(0);;
                    optionsButton[2].addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            for(ActionListener al : original)
                                optionsButton[2].addActionListener(al);
                            for(ActionListener al : original2)
                                optionsButton[3].addActionListener(al);
                            optionsButton[2].removeActionListener(this);
                            optionsButton[3].removeActionListener(shutdownListener);
                            story.reset();
                            showNode(story.getInitialNode());
                        }
                    });
                    optionsButton[3].setText("Quit");
                    optionsButton[3].setEnabled(true);
                    for(ActionListener al : original2)
                    {
                        optionsButton[3].removeActionListener(al);
                    }
                    optionsButton[3].addActionListener(shutdownListener);
                }
            }
        }
        catch(

        BSTException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error on node " + storyNode.getId() + " :" + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetOptions()
    {
        for(int i = 0; i < optionsButton.length; i++)
        {
            options[i] = null;
            JButton button = optionsButton[i];
            button.setForeground(normalButtonFg);
            button.setEnabled(false);
            button.setText("");
        }
    }

    private void optionSelected(NodeOption nodeOption) throws BSTException
    {
        for(ScriptAction oa : nodeOption.getDoOnClickActions())
            oa.exec();
        showNode(story.getNode(nodeOption.getNextNode()));
    }

}
