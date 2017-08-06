/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import java.util.Collection;
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
    public static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+");

    /**
     * Replace all the variables placeholders from the virtual node (which can
     * be a text node!) by their real value from the story.
     *
     * @param virtualNode
     *            The virtual node
     * @param story
     *            the story from which to pick the variables
     * @return The solved virtual node's text
     * @throws BSTException
     *             If an exception occurs while solving
     */
    public static String solveVariables(final VirtualNode virtualNode, final BranchingStory story)
            throws NodeNotFoundException, BSTException
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
                StoryNode node = parseNode(s, story);
                if(!(node instanceof VirtualNode))
                {
                    throw new BSTException(-1, "Node is not a virtual node : " + node.getId(),
                            story);
                }
                text = text.replace(toReplace, ((VirtualNode)node).getText());
            }
            else if(varName.startsWith("&"))
            {
                final String s = varName.substring(1);
                StoryNode i = parseNode(s, story);
                if(!(i instanceof LogicalNode))
                {
                    throw new BSTException(-1, "Node " + i.getId() + " (alias : "
                            + i.getTag("alias") + ") is not a logical node", story);
                }
                final LogicalNode ln = (LogicalNode)i;
                i = ln.solve(story);
                while(i instanceof LogicalNode)
                {
                    i = ((LogicalNode)i).solve(story);
                }
                if(i == null)
                {
                    // Should already be covered, but let's crash anyway, who knows what can happen?
                    throw new BSTException(-1, "Node does not exist", story);
                }
                text = text.replace(toReplace, ((VirtualNode)i).getText());
            }
            else
            {
                text = vn.replaceFirst(
                        story.getRegistry().get(varName, Integer.valueOf(0)).toString());
            }
            vn.reset(text);
        }

        return text;
    }

    public static StoryNode parseNode(String toParse, BranchingStory story)
            throws NodeNotFoundException, BSTException
    {
        StoryNode sn = null;
        Matcher m = NUMBER_PATTERN.matcher(toParse);
        // Is it a number? If yes, return the appropriate node
        if(m.matches())
        {
            int i = Integer.parseInt(toParse);
            sn = story.getNode(i);
            if(sn == null && i != -1)
            {
                throw new NodeNotFoundException(i, story.getTag("__sourcename"));
            }
        }
        else
        {
            Collection<StoryNode> nodes = story.getAllNodes();
            // Is it an alias? Try to find an alias
            for(StoryNode node : nodes)
            {
                String aliasTag = node.getTag("alias");
                if(toParse.equals(aliasTag))
                {
                    sn = node;
                }
            }
            if(sn == null)
            {
                // If it's not an alias, try to find the variable
                Object obj = story.getRegistry().get(toParse, null);
                if(obj == null)
                {
                    // Not a variable either. Throw an exception
                    throw new BSTException(-1, "Unknown alias or variable : " + toParse, story);
                }

                if(obj instanceof Integer)
                {
                    // The variable is an integer. It's the id we're looking for!
                    Integer i = (Integer)obj;
                    sn = story.getNode(i);
                    if(sn == null)
                    {
                        throw new NodeNotFoundException(i, story.getTag("__sourcename"));
                    }
                }
                else if(obj instanceof String)
                {
                    // A string! We assume it's an alias
                    String s = (String)obj;
                    for(StoryNode node : nodes)
                    {
                        if(s.equals(node.getTag("alias")))
                        {
                            return node;
                        }
                    }
                    throw new BSTException(-1, "Unknown alias : " + s, story);
                }

            }
        }
        return sn;
    }
}
