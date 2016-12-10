package utybo.branchingstorytree.brm;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

public class BRMAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, BranchingStory story, BSTClient client) throws BSTException
    {
        BRMHandler brm = client.getBRMHandler();
        if(brm == null)
            throw new BSTException(-1, "brm not supported");
        brm.loadAuto();
    }

    @Override
    public String[] getName()
    {
        return new String[]{"brm_load"};
    }

}
