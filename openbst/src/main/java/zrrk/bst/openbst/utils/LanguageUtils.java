/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.utils;

import java.util.Map.Entry;

import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.NodeOption;
import zrrk.bst.bstjava.api.story.StoryNode;
import zrrk.bst.bstjava.api.story.TextNode;
import zrrk.bst.bstjava.api.story.VirtualNode;
import zrrk.bst.openbst.OpenBST;

public class LanguageUtils
{
    private LanguageUtils()
    {}

    public static boolean checkNonLatin(BranchingStory story)
    {
        boolean hasNonLatin = false;

        // Check in the tags (titles)
        for(Entry<String, String> e : story.getTagMap().entrySet())
        {
            hasNonLatin = checkNonLatin(e.getValue());
            if(hasNonLatin)
            {
                OpenBST.LOG.info("Found non-latin in tags : " + e.getValue());
                OpenBST.LOG
                        .info("Non latin characters : " + getAllNonLatinCharacters(e.getValue()));
                break;
            }
        }
        if(!hasNonLatin) // Nothing detected in tags, check nodes
        {
            for(StoryNode sn : story.getAllNodes())
            {
                if(sn instanceof VirtualNode) // VirtualNode/TextNode => Check text
                {
                    hasNonLatin = checkNonLatin(((VirtualNode)sn).getText());
                    if(hasNonLatin)
                    {
                        OpenBST.LOG.info("Found non-latin in node " + sn.getId() + "("
                                + sn.getTagOrDefault("alias", "<none>") + ") : "
                                + ((VirtualNode)sn).getText());
                        OpenBST.LOG.info("Non latin characters : "
                                + getAllNonLatinCharacters(((VirtualNode)sn).getText()));
                        break;
                    }
                }
                if(sn instanceof TextNode) // TextNode => check options
                {
                    for(NodeOption o : ((TextNode)sn).getOptions())
                    {
                        hasNonLatin = checkNonLatin(o.getText());
                        if(hasNonLatin)
                        {

                            OpenBST.LOG.info("Found non-latin in node " + sn.getId() + "("
                                    + sn.getTagOrDefault("alias", "<none>") + ") option : "
                                    + o.getText());

                            OpenBST.LOG.info("Non latin characters : "
                                    + getAllNonLatinCharacters(o.getText()));
                            break;
                        }
                    }
                    if(hasNonLatin)
                        break;
                }
                // TODO Add checking for Logical nodes somehow
            }
        }
        return hasNonLatin;
    }

    public static boolean checkNonLatin(String s)
    {
        for(char c : s.toCharArray())
        {
            if(c > 0x17f)
            {
                return true;
            }
        }
        return false;
    }

    public static String getAllNonLatinCharacters(final String s)
    {
        String sx = "";
        for(char c : s.toCharArray())
        {
            if(c > 0x17f)
                sx += c;
        }
        return sx;
    }

}
