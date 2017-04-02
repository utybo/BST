/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story.logicalnode;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.story.LogicalNode;

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
    public int execute() throws BSTException
    {
        return innd.getNextNode();
    }
}
