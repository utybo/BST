package utybo.branchingstorytree.api.story.logicalnode;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.IfNextNodeDefiner;

public class LNCondReturn extends LNInstruction
{
    private final IfNextNodeDefiner innd;

    public LNCondReturn(final IfNextNodeDefiner innd)
    {
        this.innd = innd;
    }

    @Override
    public int execute() throws BSTException
    {
        return innd.getNextNode();
    }
}
