package utybo.branchingstorytree.api.story.logicalnode;

import java.util.ArrayList;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.ScriptChecker;

public class LNTern extends LNInstruction
{
    private ArrayList<ScriptAction> trueActions;
    private ArrayList<ScriptAction> falseActions;
    private ArrayList<ScriptChecker> checkers;

    public LNTern(ArrayList<ScriptChecker> checker, ArrayList<ScriptAction> trueActions, ArrayList<ScriptAction> falseActions)
    {
        checkers = checker;
        this.trueActions = trueActions;
        this.falseActions = falseActions;
    }

    @Override
    public int execute() throws BSTException
    {
        if(solveCheckers())
            solve(trueActions);
        else
            solve(falseActions);
        return -1;
    }

    private boolean solveCheckers() throws BSTException
    {
        boolean b = true;
        for(ScriptChecker checker : checkers)
        {
            if(!checker.check())
                b = false;
        }
        return b;
    }

    private void solve(ArrayList<ScriptAction> actions) throws BSTException
    {
        for(ScriptAction action : actions)
            action.exec();
    }
}