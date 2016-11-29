/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script.functions.action;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

public class CloneAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, BranchingStory story, BSTClient client) throws BSTException
    {
        String[] opt = desc.split(",");
        VariableRegistry registry = story.getRegistry();
        if(opt.length != 2)
        {
            throw new BSTException(-1, "Invalid syntax : clone:<to clone>,<clone into>");
        }
        if(registry.typeOf(opt[0]) == null)
        {
            throw new BSTException(-1, "Unknown variable : " + opt[0]);
        }
        try
        {
            registry.put(opt[1], (Integer)registry.get(opt[0], null));
        }
        catch(ClassCastException e)
        {
            registry.put(opt[1], registry.get(opt[0], "").toString());
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"clone"};
    }

}
