/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.utils;

import java.util.Map.Entry;

import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;

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
                break;
        }
        if(!hasNonLatin) // Nothing detected in tags, check nodes
        {
            for(StoryNode sn : story.getAllNodes())
            {
                if(sn instanceof VirtualNode) // VirtualNode/TextNode => Check text
                {
                    hasNonLatin = checkNonLatin(((VirtualNode)sn).getText());
                    if(hasNonLatin)
                        break;
                }
                if(sn instanceof TextNode) // TextNode => check options
                {
                    for(NodeOption o : ((TextNode)sn).getOptions())
                    {
                        hasNonLatin = checkNonLatin(o.getText());
                        if(hasNonLatin)
                            break;
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
            if(c < 0x21 || c > 0x17f)
            {
                return true;
            }
        }
        return false;
    }

}
