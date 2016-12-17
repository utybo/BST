/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.VirtualNode;

public class StoryUtils
{
    public static String solveVariables(final VirtualNode virtualNode, final BranchingStory story) throws BSTException
    {
        String text = virtualNode.getText();
        final Pattern vp = Pattern.compile("\\$\\{(([\\&\\>]\\d+)|([\\w_]+))\\}");
        final Matcher vn = vp.matcher(text);
        while(vn.find())
        {
            final String toReplace = vn.group();
            final String varName = toReplace.substring(2, toReplace.length() - 1);
            if(varName.startsWith(">"))
            {
                final String s = varName.substring(1);
                final int i = Integer.parseInt(s);
                text = text.replace(toReplace, ((VirtualNode)story.getNode(i)).getText());
            }
            else if(varName.startsWith("&"))
            {
                final String s = varName.substring(1);
                final int i = Integer.parseInt(s);
                final LogicalNode ln = (LogicalNode)story.getNode(i);
                StoryNode node = story.getNode(ln.solve());
                while(node instanceof LogicalNode)
                    node = story.getNode(((LogicalNode)node).solve());
                    
                text = text.replace(toReplace, ((VirtualNode)node).getText());
            }
            else
            {
                text = vn.replaceFirst(story.getRegistry().get(varName, Integer.valueOf(0)).toString());
            }
            vn.reset(text);
        }

        return text;
    }
}
