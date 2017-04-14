/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import java.util.Collection;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;

public class AliasOrVariableNextNodeDefiner implements NextNodeDefiner
{
    private String alias;

    public AliasOrVariableNextNodeDefiner(String alias)
    {
        this.alias = alias;
    }

    @Override
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException
    {
        Collection<StoryNode> nodes = story.getAllNodes();
        for(StoryNode node : nodes)
        {
            if(alias.equals(node.getTag("alias")))
                return node;
        }
        Object obj = story.getRegistry().get(alias, null);
        if(obj == null)
        {
            throw new BSTException(-1, "Unknown alias or variable : " + alias);
        }
        if(obj instanceof Integer)
        {
            Integer i = (Integer)obj;
            StoryNode sn = story.getNode(i);
            if(sn == null)
                throw new NodeNotFoundException(i, story.getTag("__sourcename"));
            return sn;
        }
        else if(obj instanceof String)
        {
            String s = (String)obj;
            for(StoryNode node : nodes)
            {
                if(s.equals(node.getTag("alias")))
                    return node;
            }
            throw new BSTException(-1, "Unknown alias : " + s);
        }
        throw new BSTException(-1, "Could not determine alias or variable : " + alias);
    }
}
