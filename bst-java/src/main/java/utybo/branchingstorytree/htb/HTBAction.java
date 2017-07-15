package utybo.branchingstorytree.htb;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

public class HTBAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client) throws BSTException
    {
        if(client.getHTBHandler() == null)
        {
            throw new BSTException(line, "HTB and is not supported.", story.getTag("__sourcename"));
        }
        switch(head)
        {
        case "htb_import":
        {
            String[] bits = desc.split(",");
            if(bits.length != 2)
            {
                throw new BSTException(line, "Invalid syntax : htb_import:resourceName,variableToPutItInto", story.getTag("__sourcename"));
            }
            String resource = bits[0];
            String var = bits[1];
            story.getRegistry().put(var, client.getHTBHandler().getAsString(resource));
            break;
        }
        case "htb_base64":
        {
            String[] bits = desc.split(",");
            if(bits.length != 2)
            {
                throw new BSTException(line, "Invalid syntax : htb_base64:resourceName,variableToPutItIntoAsBase64", story.getTag("__sourcename"));
            }
            String resource = bits[0];
            String var = bits[1];
            story.getRegistry().put(var, client.getHTBHandler().getAsBase64(resource));
            break;
        }
        }

    }

    @Override
    public String[] getName()
    {
        return new String[] {"htb_import", "htb_base64"};
    }

}
