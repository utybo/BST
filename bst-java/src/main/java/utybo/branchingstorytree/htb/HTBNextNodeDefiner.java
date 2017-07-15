package utybo.branchingstorytree.htb;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;

public class HTBNextNodeDefiner implements NextNodeDefiner
{
    private String head, desc;
    private BSTClient client;
    
    public HTBNextNodeDefiner(String head, String desc, BSTClient client)
    {
        this.head = head;
        this.desc = desc;
        this.client = client;
    }

    @Override
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException
    {
        if(client.getHTBHandler() == null)
            throw new BSTException(-1, "HTB is not supported");
        switch(head) {
        case "htb_requestjs":
        {
            StoryNode sn = null;
            if(desc.length() > 0)
            {
                NextNodeDefiner nnd;
                try
                {
                    nnd = new BranchingStoryTreeParser().parseNND(desc, new Dictionnary(), -1, story, client, story.getTag("__sourcename"));
                    sn = nnd.getNextNode(story);
                }
                catch(InstantiationException | IllegalAccessException e)
                {
                    throw new BSTException(-1, "Unexpected exception : " + e.getClass().getSimpleName() + ", " + e.getMessage(), story.getTag("__sourcename"));
                }
            }
            else
            {
                sn = null;
            }
            if(client.getHTBHandler().requestJSAccess())
                return null;
            else
                return sn;
        }
        case "htb_requesthref":
        {
            StoryNode sn = null;
            if(desc.length() > 0)
            {
                NextNodeDefiner nnd;
                try
                {
                    nnd = new BranchingStoryTreeParser().parseNND(desc, new Dictionnary(), -1, story, client, story.getTag("__sourcename"));
                    sn = nnd.getNextNode(story);
                }
                catch(InstantiationException | IllegalAccessException e)
                {
                    throw new BSTException(-1, "Unexpected exception : " + e.getClass().getSimpleName() + ", " + e.getMessage(), story.getTag("__sourcename"));
                }
            }
            else
            {
                sn = null;
            }
            if(client.getHTBHandler().requestHrefAccess())
            {
                return null;
            }
            else
            {
                return sn;
            }
        }
        }
        return null;
    }

}
