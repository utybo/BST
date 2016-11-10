package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;

public class ActionDescriptor
{
    private ScriptAction action;
    private String head, desc;
    private BSTClient client;
    private VariableRegistry registry;

    public ActionDescriptor(ScriptAction action, String head, String desc, VariableRegistry registry, BSTClient client)
    {
        this.action = action;
        this.head = head;
        this.desc = desc;
        this.client = client;
        this.registry = registry;
    }

    public void exec() throws BSTException
    {
        action.exec(head, desc, registry, client);
    }

    public ScriptAction getAction()
    {
        return action;
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
