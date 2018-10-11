/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.story.logicalnode;

import java.util.ArrayList;

import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.ActionDescriptor;
import zrrk.bst.bstjava.api.script.CheckerDescriptor;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.StoryNode;

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
    public LNTern(final ArrayList<CheckerDescriptor> checker,
            final ArrayList<ActionDescriptor> trueActions,
            final ArrayList<ActionDescriptor> falseActions)
    {
        checkers = checker;
        this.trueActions = trueActions;
        this.falseActions = falseActions;
    }

    @Override
    public StoryNode execute(BranchingStory story) throws BSTException
    {
        if(solveCheckers())
        {
            solve(trueActions);
        }
        else
        {
            solve(falseActions);
        }
        return null;
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
    
    public ArrayList<ActionDescriptor> getTrueActions()
    {
        return new ArrayList<>(trueActions);
    }
    
    public ArrayList<ActionDescriptor> getFalseActions()
    {
        return new ArrayList<>(falseActions);
    }
    
    public ArrayList<CheckerDescriptor> getCheckers()
    {
        return new ArrayList<>(checkers);
    }
}