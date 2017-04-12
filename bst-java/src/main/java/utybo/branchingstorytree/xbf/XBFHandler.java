/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.xbf;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface XBFHandler extends BRMResourceConsumer
{
    public BranchingStory getAdditionalStory(String name);

    public BranchingStory getMainStory();

    /**
     * Do a basic setup of a newly added story. Make sure this is called every
     * time you load a story!
     * 
     * @param original
     *            The "main" story
     * @param target
     *            The story newly loaded
     */
    public default void bind(BranchingStory original, BranchingStory target, String name) throws BSTException
    {
        target.setRegistry(original.getRegistry());
    }
}
