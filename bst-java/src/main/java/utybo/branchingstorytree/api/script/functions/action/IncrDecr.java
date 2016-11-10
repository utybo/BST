package utybo.branchingstorytree.api.script.functions.action;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;

public class IncrDecr implements ScriptAction
{

    @Override
    public void exec(String head, String desc, VariableRegistry registry, BSTClient client) throws BSTException
    {
        boolean incr;
        switch(head)
        {
        case "incr":
            incr = true;
            break;
        case "decr":
            incr = false;
            break;
        default:
            throw new BSTException(-1, "Internal error");
        }
        if(registry.typeOf(desc) != null && registry.typeOf(desc) != Integer.class)
        {
            throw new BSTException(-1, (incr ? "incr" : "decr") + " : The variable " + desc + " is not a number.");
        }
        registry.put(desc, (Integer)registry.get(desc, 0) + (incr ? 1 : -1));

    }

    @Override
    public String[] getName()
    {
        return new String[] {"incr", "decr"};
    }

}
