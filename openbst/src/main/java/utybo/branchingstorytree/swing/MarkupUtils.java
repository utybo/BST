package utybo.branchingstorytree.swing;

import org.apache.commons.lang.StringEscapeUtils;

import com.github.rjeschke.txtmark.Processor;

import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;

public class MarkupUtils
{
    public static int solveMarkup(final BranchingStory story, final TextNode textNode)
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

    public static  String translateMarkup(int markupLanguage, String input)
    {
        switch(markupLanguage)
        {
        case 1:
            return "<html>" + Processor.process(input); // MD to HTML
        // TODO Test to see if HTML characters are escaped
        case 2:
            return "<html>" + input; // HTML to HTML
        default:
            return "<html>" + StringEscapeUtils.escapeHtml(input).replace("\n", "<br>"); // Plain text to HTML
        }
    }
}
