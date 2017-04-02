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

/**
 * Utility methods that are useful when creating your implementation
 *
 * @author utybo
 *
 */
public class StoryUtils
{
    /**
     * Replace all the variables placeholders from the virtual node by their
     * real value from the story.
     *
     * @param virtualNode
     *            The virtual node
     * @param story
     *            the story from which to pick the variables
     * @return The solved virtual node's text
     * @throws BSTException
     *             If an exception occurs while solving
     */
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
                int i = Integer.parseInt(s);
                final LogicalNode ln = (LogicalNode)story.getNode(i);
                i = ln.solve();
                StoryNode node = story.getNode(i);
                while(node instanceof LogicalNode)
                {
                    i = ((LogicalNode)node).solve();
                    node = story.getNode(i);
                }
                if(node == null)
                {
                    throw new BSTException(-1, "Node does not exist : " + i);
                }
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
