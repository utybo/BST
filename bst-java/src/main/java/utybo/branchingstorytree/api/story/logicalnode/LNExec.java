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
import utybo.branchingstorytree.api.script.ActionDescriptor;

/**
 * An instruction that executes an action
 * 
 * @author utybo
 *
 */
public class LNExec extends LNInstruction
{
    private final ActionDescriptor action;

    /**
     * Create an LNExec
     * 
     * @param action
     *            The action to be wrapped
     */
    public LNExec(final ActionDescriptor action)
    {
        this.action = action;
    }

    @Override
    public int execute() throws BSTException
    {
        action.exec();
        return -1;
    }
}
