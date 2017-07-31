/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.utils;

import org.apache.commons.lang.StringEscapeUtils;

import com.github.rjeschke.txtmark.Processor;

import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;

public class MarkupUtils
{
    public static int solveMarkup(final BranchingStory story, final TextNode textNode)
    {
        if(story.hasTag("markup") || textNode != null && textNode.hasTag("markup"))
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

    public static String translateMarkup(final int markupLanguage, final String input)
    {
        switch(markupLanguage)
        {
        case 1:
            return Processor.process(input).substring("<p>".length()); // MD to HTML (strip the <p>)
        case 2:
            return input; // HTML to HTML
        default:
            return StringEscapeUtils.escapeHtml(input).replace("\n\n", "<p>").replace("\n", "<br>"); // Plain text to HTML
        }
    }

    public static String toHex(int r, int g, int b)
    {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number)
    {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while(builder.length() < 2)
        {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

}
