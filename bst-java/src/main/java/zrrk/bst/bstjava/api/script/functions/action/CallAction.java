/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script.functions.action;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.StoryUtils;
import zrrk.bst.bstjava.api.script.ScriptAction;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.LogicalNode;
import zrrk.bst.bstjava.api.story.StoryNode;

/**
 * Implementation of the call action
 *
 * @author utybo
 *
 */
public class CallAction implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        StoryNode node = StoryUtils.parseNode(desc, story);
        if(!(node instanceof LogicalNode))
        {
            throw new BSTException(-1, desc + " is not a logical node", story);
        }
        ((LogicalNode)node).solve(story);
    }

    @Override
    public String[] getName()
    {
        return new String[] {"call"};
    }

}
