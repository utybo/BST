package utybo.branchingstorytree.xsf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.VirtualNode;

@Experimental
public class XSFexNextNodeDefiner implements NextNodeDefiner
{
    private final String desc;
    private final int line;
    private final BSTClient client;

    public XSFexNextNodeDefiner(String head, String desc, int line, BSTClient client)
    {
        this.line = line;
        this.desc = desc;
        this.client = client;
    }

    @Override
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException
    {
        // TODO finish
        // run the script like the xsf action, then check for the output
        // - if it's a story node, that's our next node
        // - if it's anything else that is not null, convert to String and put that
        //   in a virtual node, that's our next node
        // - if it's null, return an empty virtual node

        String[] bits = desc.split(",");
        XSFHandler xsf = client.getXSFHandler();
        if(bits.length < 2 || bits.length > 3)
        {
            throw new BSTException(line,
                    "Incorrect syntax : xsf_exec:filename,function OR xsf_exec:filename,function,putIn",
                    story);
        }
        if(xsf == null)
            throw new BSTException(line, "XSF is not supported.", story);
        Object ret = xsf.invokeScript(bits[0], bits[1], new XSFBridge(story, client, line), story,
                line);
        if(ret instanceof StoryNode)
            return (StoryNode)ret;
        VirtualNode vn = new VirtualNode(-1, null);
        if(ret != null)
        {
            vn.setText(ret.toString());
        }
        return vn;
    }

}
