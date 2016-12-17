/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
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

    public static String translateMarkup(int markupLanguage, String input)
    {
        switch(markupLanguage)
        {
        case 1:
            return "<html>" + Processor.process(input).replaceAll("<p>", "<p><p>").substring(3); // MD to HTML
        // TODO Test to see if HTML characters are escaped
        case 2:
            return "<html>" + input; // HTML to HTML
        default:
            return "<html>" + StringEscapeUtils.escapeHtml(input).replace("\n", "<br>"); // Plain text to HTML
        }
    }
}
