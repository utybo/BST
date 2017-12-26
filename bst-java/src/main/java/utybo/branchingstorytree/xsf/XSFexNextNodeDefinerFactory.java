package utybo.branchingstorytree.xsf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.api.script.ExtNNDFactory;
import utybo.branchingstorytree.api.script.NextNodeDefiner;

@Experimental
public class XSFexNextNodeDefinerFactory implements ExtNNDFactory
{

    @Override
    public NextNodeDefiner createNND(String head, String desc, int line, BSTClient client)
    {
        return new XSFexNextNodeDefiner(head, desc, line, client);
    }

    @Override
    public String[] getNames()
    {
        return new String[] {"xsfex_return"};
    }

}
