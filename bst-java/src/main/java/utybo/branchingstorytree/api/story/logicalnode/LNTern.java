package utybo.branchingstorytree.api.story.logicalnode;

import java.util.ArrayList;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.ScriptChecker;

public class LNTern extends LNInstruction
{
    private final ArrayList<ScriptAction> trueActions;
    private final ArrayList<ScriptAction> falseActions;
    private final ArrayList<ScriptChecker> checkers;

    public LNTern(final ArrayList<ScriptChecker> checker, final ArrayList<ScriptAction> trueActions, final ArrayList<ScriptAction> falseActions)
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
        for(final ScriptChecker checker : checkers)
        {
            if(!checker.check())
            {
                b = false;
            }
        }
        return b;
    }

    private void solve(final ArrayList<ScriptAction> actions) throws BSTException
    {
        for(final ScriptAction action : actions)
        {
            action.exec();
        }
    }
}