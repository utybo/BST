/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.xsf;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.ScriptAction;
import zrrk.bst.bstjava.api.story.BranchingStory;

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
