/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
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
    private final String head, desc;
    private final BSTClient client;

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
        {
            throw new BSTException(-1, "HTB is not supported", story);
        }
        switch(head)
        {
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
            {
                return null;
            }
            else
            {
                return sn;
            }
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
