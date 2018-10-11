/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.story.logicalnode;

import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.NextNodeDefiner;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.LogicalNode;
import zrrk.bst.bstjava.api.story.StoryNode;

/**
 * A return statement in a {@link LogicalNode} that determines the next node
 * depending on a {@link NextNodeDefiner}
 *
 * @author utybo
 *
 */
public class LNCondReturn extends LNInstruction
{
    private final NextNodeDefiner innd;

    /**
     * Create an LNCondReturn
     *
     * @param innd
     *            The NextNodeDefiner to use
     */
    public LNCondReturn(final NextNodeDefiner innd)
    {
        this.innd = innd;
    }

    @Override
    public StoryNode execute(BranchingStory story) throws BSTException
    {
        StoryNode next = innd.getNextNode(story);
        return next;
    }
    
    public NextNodeDefiner getNND()
    {
        return innd;
    }
}
