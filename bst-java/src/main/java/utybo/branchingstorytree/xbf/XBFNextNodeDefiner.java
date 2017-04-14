/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.xbf;

import java.util.Collection;
import java.util.regex.Pattern;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;

public class XBFNextNodeDefiner implements NextNodeDefiner
{
    private String desc;
    private BSTClient client;

    public XBFNextNodeDefiner(String head, String desc, BSTClient client)
    {
        this.desc = desc;
        this.client = client;
    }

    @Override
    public StoryNode getNextNode(BranchingStory bs) throws BSTException
    {
        XBFHandler xbf = client.getXBFHandler();
        if(xbf == null)
            throw new BSTException(-1, "XBF not supported");
        String[] args = desc.split(",");
        if(args.length == 2)
        {
            String from = args[0];
            String id = args[1];
            StoryNode node = null;
            if(Pattern.compile("\\d+").matcher(id).matches())
            {
                Integer intId = Integer.parseInt(id);
                BranchingStory story2 = xbf.getAdditionalStory(from);
                if(story2 == null)
                    throw new BSTException(-1, story2 + " doesn't exist");
                node = story2.getNode(intId);
                if(node == null)
                    throw new NodeNotFoundException(intId, from);
            }
            else
            {
                // Assume id is an alias
                Collection<StoryNode> nodes = bs.getAllNodes();
                for(StoryNode node2 : nodes)
                {
                    if(id.equals(node2.getTag("alias")))
                        node = node2;
                }
                if(node == null)
                    throw new BSTException(-1, "Unknown alias : " + id + ", from " + from);
            }
            if(node instanceof VirtualNode && !(node instanceof TextNode)) 
                // Check if it's just a virtualnode and not a textnode
                // This trick is required as TextNodes are a subset of VirtualNodes
                throw new BSTException(-1, "Node " + id + " from " + from + " is a virtual node and thus cannot be the next node");
            return node;
        }
        else if(args.length == 1)
        {
            Integer id = Integer.parseInt(args[0]);
            StoryNode node = xbf.getMainStory().getNode(id);
            if(node == null)
                throw new NodeNotFoundException(id, "<main>");
            if(node instanceof VirtualNode && !(node instanceof TextNode)) 
                // Check if it's just a virtualnode and not a textnode
                // This trick is required as TextNodes are a subset of VirtualNodes
                throw new BSTException(-1, "Node " + id + " from <main> is a virtual node and thus cannot be the next node");

            return node;
        }
        else
        {
            throw new BSTException(-1, "Incorrect syntax : xbf_call:fromfile,node OR to call a node from the main BST file xbf_call:id");
        }
    }

}
