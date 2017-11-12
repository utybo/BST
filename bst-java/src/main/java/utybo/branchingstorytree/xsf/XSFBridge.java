package utybo.branchingstorytree.xsf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

public class XSFBridge
{
    private final BranchingStory story;
    private final BSTClient client;
    private final int line;

    public XSFBridge(BranchingStory story, BSTClient client, int line)
    {
        this.story = story;
        this.client = client;
        this.line = line;
    }
    
    public Object get(String name)
    {
        return story.getRegistry().get(name, null);
    }

    public void exec(String head, Object desc) throws BSTException
    {
        ScriptAction act = story.getDictionary().getAction(head);
        if(act == null)
            throw new BSTException(line, "Unknown action : " + head, story);
        act.exec(head, desc.toString(), line, story, client);
    }
    
    public void export(String varName, Object value)
    {
        XSFAction.convertAndSaveVariable(varName, value, story.getRegistry());
    }
}
