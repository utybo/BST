package utybo.branchingstorytree.htb;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.script.ExtNNDFactory;
import utybo.branchingstorytree.api.script.NextNodeDefiner;

public class HTBNextNodeDefinerFactory implements ExtNNDFactory
{

    @Override
    public NextNodeDefiner createNND(String head, String desc, BSTClient client)
    {
        return new HTBNextNodeDefiner(head, desc, client);
    }

    @Override
    public String[] getNames()
    {
        return new String[] {"htb_requestjs", "htb_requesthref"};
    }

}
