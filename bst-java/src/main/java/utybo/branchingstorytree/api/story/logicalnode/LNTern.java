/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story.logicalnode;

import java.util.ArrayList;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.script.CheckerDescriptor;

/**
 * Instruction implementation of ternary statements (if-then-else)
 *
 * @author utybo
 *
 */
public class LNTern extends LNInstruction
{
    private final ArrayList<ActionDescriptor> trueActions;
    private final ArrayList<ActionDescriptor> falseActions;
    private final ArrayList<CheckerDescriptor> checkers;

    /**
     *
     * @param checker
     *            The checkers to be checked. If there are multiple checkers,
     *            the ternary statement performs an "AND" operation.
     * @param trueActions
     *            The actions to execute if the checker(s) (all) return true
     * @param falseActions
     *            The actions to execute if (one of) the checker(s) returns
     *            false
     */
    public LNTern(final ArrayList<CheckerDescriptor> checker, final ArrayList<ActionDescriptor> trueActions, final ArrayList<ActionDescriptor> falseActions)
    {
        checkers = checker;
        this.trueActions = trueActions;
        this.falseActions = falseActions;
    }

    @Override
    public int execute() throws BSTException
    {
        if(solveCheckers())
        {
            solve(trueActions);
        }
        else
        {
            solve(falseActions);
        }
        return -1;
    }

    private boolean solveCheckers() throws BSTException
    {
        boolean b = true;
        for(final CheckerDescriptor checker : checkers)
        {
            if(!checker.check())
            {
                b = false;
            }
        }
        return b;
    }

    private void solve(final ArrayList<ActionDescriptor> actions) throws BSTException
    {
        for(final ActionDescriptor action : actions)
        {
            action.exec();
        }
    }
}