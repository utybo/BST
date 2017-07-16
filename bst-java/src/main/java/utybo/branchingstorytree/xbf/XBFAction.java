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
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;

public class XBFAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client) throws BSTException
    {
        XBFHandler xbf = client.getXBFHandler();
        if(xbf == null)
            throw new BSTException(line, "XBF not supported", story);
        String[] args = desc.split(",");
        if(args.length == 2)
        {
            String from = args[0];
            String id = args[1];
            BranchingStory story2 = xbf.getAdditionalStory(from);
            if(story2 == null)
                throw new BSTException(line, from + " doesn't exist", story);
            StoryNode node = StoryUtils.parseNode(id, story2);
            if(!(node instanceof LogicalNode))
                throw new BSTException(line, "Node " + id + " from " + from + " is not a logical node and thus cannot be called", story);
            LogicalNode lnode = (LogicalNode)node;
            lnode.solve(story); // TODO check if referencing the story in the errors should be done or not
        }
        else if(args.length == 1)
        {
            Integer id = Integer.parseInt(args[0]);
            StoryNode node = xbf.getMainStory().getNode(id);
            if(node == null)
                throw new NodeNotFoundException(id, "<main>");
            if(!(node instanceof LogicalNode))
                throw new BSTException(line, "Node " + id + " from the main file is not a logical node and thus cannot be called", story);
            ((LogicalNode)node).solve(story);
        }
        else
        {
            throw new BSTException(-1, "Incorrect syntax : xbf_call:fromfile,node OR to call a node from the main BST file xbf_call:id", story);
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"xbf_call"};
    }

}
