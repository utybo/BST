package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;

public class CheckerDescriptor
{
    private ScriptChecker checker;
    private String head, desc;
    private BSTClient client;
    private VariableRegistry registry;

    public CheckerDescriptor(ScriptChecker checker, String head, String desc, VariableRegistry registry, BSTClient client)
    {
        this.checker = checker;
        this.head = head;
        this.desc = desc;
        this.client = client;
        this.registry = registry;
    }

    public boolean check() throws BSTException
    {
        return checker.check(head, desc, registry, client);
    }

    public ScriptChecker getChecker()
    {
        return checker;
    }

    public String getHead()
    {
        return head;
    }

    public String getDesc()
    {
        return desc;
    }

    public BSTClient getClient()
    {
        return client;
    }

    public VariableRegistry getRegistry()
    {
        return registry;
    }
    
    
}
