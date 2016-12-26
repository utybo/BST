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

public class CheckerDescriptor
{
    private final ScriptChecker checker;
    private final String head, desc;
    private final BSTClient client;
    private final BranchingStory story;
    private final int debugLine;

    public CheckerDescriptor(final ScriptChecker checker, final String head, final String desc, final int debugLine, final BranchingStory story, final BSTClient client) throws BSTException
    {
        if(checker == null)
        {
            throw new BSTException(-1, "Checker " + head + " does not exist");
        }
        this.checker = checker;
        this.head = head;
        this.desc = desc;
        this.client = client;
        this.story = story;
        this.debugLine = debugLine;
    }

    public boolean check() throws BSTException
    {
        return checker.check(head, desc, debugLine, story, client);
    }

    public ScriptChecker getChecker()
    {
        return checker;
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
