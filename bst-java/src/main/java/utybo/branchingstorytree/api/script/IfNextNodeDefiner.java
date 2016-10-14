package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTException;

public class IfNextNodeDefiner implements NextNodeDefiner
{
    private int one, two;
    private ScriptChecker checker;
    
    public IfNextNodeDefiner(int one, int two, ScriptChecker checker)
    {
        this.one = one;
        this.two = two;
        this.checker = checker;
    }

    @Override
    public int getNextNode() throws BSTException
    {
        return checker.check() ? one : two;
    }

}
