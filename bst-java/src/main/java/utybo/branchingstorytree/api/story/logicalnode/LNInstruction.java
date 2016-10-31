package utybo.branchingstorytree.api.story.logicalnode;

import utybo.branchingstorytree.api.BSTException;

public abstract class LNInstruction
{
    public abstract int execute() throws BSTException;
}
