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
import utybo.branchingstorytree.api.story.LogicalNode;

/**
 * An instruction is a bit of the {@link LogicalNode} (typically a line) that is
 * executed. The contract is that instructions are executed in the order of
 * their declaration in the BST file.
 *
 * @author utybo
 *
 */
public abstract class LNInstruction
{
    /**
     * Execute this instruction
     *
     * @return The next node if this instruction is supposed to get the story
     *         forward to another node, or -1 if the execution of the
     *         {@link LogicalNode} should continue
     * @throws BSTException
     *             If an exception occurs during the execution of this
     *             instruction
     */
    public abstract int execute() throws BSTException;
}
