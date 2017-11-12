package utybo.branchingstorytree.xsf;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface XSFHandler extends BRMResourceConsumer
{
    public Object invokeScript(String resourceName, String function, XSFBridge bst,
            BranchingStory story, int line) throws BSTException;
}
