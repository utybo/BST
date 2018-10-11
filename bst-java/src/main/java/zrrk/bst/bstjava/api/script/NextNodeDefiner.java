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
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.StoryNode;

/**
 * A next node definer is used to guess which node should be next. Each NND
 * should store required information from their constructor.
 *
 * @author utybo
 *
 */
public interface NextNodeDefiner
{
    /**
     * Determine the next node.
     *
     * @return The next node
     * @throws BSTException
     *             If an exception occurs during the process
     */
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException;
}
