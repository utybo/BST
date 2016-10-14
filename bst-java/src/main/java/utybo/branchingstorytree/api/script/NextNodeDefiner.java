package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTException;

public interface NextNodeDefiner
{
    public int getNextNode() throws BSTException;
}
