package utybo.branchingstorytree.api.story.logicalnode;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;

public class LNExec extends LNInstruction
{
    private ScriptAction action;

    public LNExec(ScriptAction action)
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
