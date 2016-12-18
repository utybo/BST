/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;

public class ActionDescriptor
{
    private ScriptAction action;
    private String head, desc;
    private BSTClient client;
    private BranchingStory story;
    private int debugLine;

    public ActionDescriptor(ScriptAction action, String head, String desc, int debugLine, BranchingStory story, BSTClient client) throws BSTException
    {
        if(action == null)
        {
            throw new BSTException(debugLine, "Action " + head + " does not exist");
        }
        this.action = action;
        this.head = head;
        this.desc = desc;
        this.client = client;
        this.story = story;
        this.debugLine = debugLine;
    }

    public void exec() throws BSTException
    {
        action.exec(head, desc, debugLine, story, client);
    }

    public ScriptAction getAction()
    {
        return action;
    }

    public String getHead()
    {
        return head;
    }

    public String getDesc()
    {
        return desc;
    }

    public BSTClient getClient()
    {
        return client;
    }

    public BranchingStory getStory()
    {
        return story;
    }

    public int getLine()
    {
        return debugLine;
    }
}
