/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.utils;

import com.github.rjeschke.txtmark.Processor;

import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.TextNode;

public class MarkupUtils
{
    public static int solveMarkup(final BranchingStory mainStory, final BranchingStory story,
            final TextNode textNode)
    {
        if(mainStory.hasTag("markup") || (story != null && story.hasTag("markup"))
                || (textNode != null && textNode.hasTag("markup")))
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
            else if(story != null && story.hasTag("markup"))
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
            else if(mainStory.hasTag("markup"))
            {
                final String s = mainStory.getTag("markup");
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
            return escapeHTML(input).replace("\n\n", "<p>").replace("\n", "<br>"); // Plain text to HTML
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

    public static String escapeHTML(String s)
    {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c > 127 || c == '"' || c == '<' || c == '>' || c == '&')
            {
                out.append("&#");
                out.append((int)c);
                out.append(';');
            }
            else
            {
                out.append(c);
            }
        }
        return out.toString();
    }
}
