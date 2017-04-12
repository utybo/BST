/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.xbf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;

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
            Integer intId = Integer.parseInt(id);
            BranchingStory story2 = xbf.getAdditionalStory(from);
            if(story2 == null)
                throw new BSTException(-1, story2 + " doesn't exist");
            StoryNode node = story2.getNode(intId);
            if(node == null)
                throw new NodeNotFoundException(intId, from);
            if(!(node instanceof TextNode))
                throw new BSTException(-1, "Node " + id + " from " + from + " is not a text node and thus cannot be the next node");
            return node;
        }
        else if(args.length == 1)
        {
            System.out.println(args[0]);
            Integer id = Integer.parseInt(args[0]);
            System.out.println(id);
            StoryNode node = xbf.getMainStory().getNode(id);
            if(node == null)
                throw new NodeNotFoundException(id, "<main>");
            if(!(node instanceof TextNode))
                throw new BSTException(-1, "Node " + id + " from the main BST file is not a text node and thus cannot be the next node");

            return node;
        }
        else
        {
            throw new BSTException(-1, "Incorrect syntax : xbf_call:fromfile,node OR to call a node from the main BST file xbf_call:id");
        }
    }

}
