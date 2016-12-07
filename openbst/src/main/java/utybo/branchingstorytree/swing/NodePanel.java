package utybo.branchingstorytree.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;

@SuppressWarnings("serial")
public class NodePanel extends JScrollablePanel
{
    private JLabel textLabel;

    public NodePanel()
    {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        textLabel = new JLabel(Lang.get("story.problem"));
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setFont(new JTextArea().getFont());
        textLabel.setForeground(Color.BLACK);
        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(textLabel, BorderLayout.CENTER);
    }

    public void applyNode(BranchingStory story, TextNode textNode) throws BSTException
    {
        String text = StoryUtils.solveVariables(textNode, story);
        final int markupLanguage = MarkupUtils.solveMarkup(story, textNode);
        setText(MarkupUtils.translateMarkup(markupLanguage, text));

        if(textNode.hasTag("color"))
        {
            final String color = textNode.getTag("color");
            setTextColor(color);
        }
        else
        {
            setTextColor(Color.BLACK);
        }
    }

    public void setText(String text)
    {
        textLabel.setText(text);
    }

    public void setTextColor(String color)
    {
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
                // TODO add a warning?
                System.err.println("COLOR DOES NOT EXIST : " + color);
                e.printStackTrace();
            }
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

    public void setTextColor(Color color)
    {
        textLabel.setForeground(color);
    }

}
