/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script;

import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.NodeNotFoundException;
import zrrk.bst.bstjava.api.StoryUtils;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.StoryNode;

public class SimpleNextNodeDefiner implements NextNodeDefiner
{
    public final String alias;

    public SimpleNextNodeDefiner(String alias)
    {
        this.alias = alias;
    }

    @Override
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException
    {
        return StoryUtils.parseNode(alias, story);
    }
}
