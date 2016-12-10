package utybo.branchingstorytree.ssb;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

public class SSBAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, BranchingStory story, BSTClient client) throws BSTException
    {
        SSBHandler ssb = client.getSSBHandler();
        if(ssb == null)
            throw new BSTException(-1, "ssb not supported");
        head = head.substring(4);
        switch(head)
        {
        case "play":
            ssb.play(desc);
            break;
        case "ambient":
            ssb.ambient(desc);
            break;
        case "stop":
            ssb.stop();
            break;
        }
    }

    @Override
    public String[] getName()
    {
        return new String[]{"ssb_play", "ssb_stop", "ssb_ambient"};
    }

}
