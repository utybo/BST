/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.htb;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.Experimental;
import zrrk.bst.bstjava.api.script.ScriptAction;
import zrrk.bst.bstjava.api.story.BranchingStory;

@Experimental
public class HTBexAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client)
            throws BSTException
    {
        if(client.getHTBHandler() == null)
            throw new BSTException(line, "HTB not supported", story);
        String[] bits = desc.split(",");
        if(head.equals("htbex_cssapply"))
        {
            for(String bit : bits)
            {
                client.getHTBHandler().applyCSS(bit);
            }
        }
        else if(head.equals("htbex_cssremove"))
        {
            for(String bit : bits)
            {
                client.getHTBHandler().removeCSS(bit);
            }
        }
        else if(head.equals("htbex_cssclear"))
        {
            client.getHTBHandler().clearCSS();
        }
    }

    @Override
    public String[] getName()
    {
        return new String[]{"htbex_cssapply", "htbex_cssremove", "htbex_cssclear"};
    }

}
