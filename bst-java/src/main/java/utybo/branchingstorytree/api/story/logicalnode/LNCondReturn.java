package utybo.branchingstorytree.api.story.logicalnode;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.IfNextNodeDefiner;

public class LNCondReturn extends LNInstruction
{
    private IfNextNodeDefiner innd;

    public LNCondReturn(IfNextNodeDefiner innd)
    {
        this.innd = innd;
    }

    @Override
    public int execute() throws BSTException
    {
        return innd.getNextNode();
    }
}
