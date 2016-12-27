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

/**
 * Implementation of the incr and decr actions
 * 
 * @author utybo
 *
 */
public class IncrDecr implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
        boolean incr;
        switch(head)
        {
        case "incr":
            incr = true;
            break;
        case "decr":
            incr = false;
            break;
        default:
            throw new BSTException(line, "Internal error");
        }
        if(registry.typeOf(desc) != null && registry.typeOf(desc) != Integer.class)
        {
            throw new BSTException(line, (incr ? "incr" : "decr") + " : The variable " + desc + " is not a number.");
        }
        registry.put(desc, (Integer)registry.get(desc, 0) + (incr ? 1 : -1));

    }

    @Override
    public String[] getName()
    {
        return new String[] {"incr", "decr"};
    }

}
