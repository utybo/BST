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

public class BoundAction implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
        final String[] bits = desc.split(",");
        if(bits.length != 3)
        {
            throw new BSTException(line, "Invalid syntax : bound:tocheck,min,max");
        }
        if(registry.typeOf(bits[0]) != Integer.class && registry.typeOf(bits[0]) != null)
        {
            throw new BSTException(line, "Unknown variable : " + bits[0]);
        }
        int toCheck = (Integer)registry.get(bits[0], 0);
        final int min = registry.typeOf(bits[1]) == Integer.class ? (Integer)registry.get(bits[1], 0) : Integer.parseInt(bits[1]);
        final int max = registry.typeOf(bits[2]) == Integer.class ? (Integer)registry.get(bits[2], 0) : Integer.parseInt(bits[2]);
        if(max < min)
        {
            throw new BSTException(line, "min < max");
        }
        if(toCheck < min)
        {
            toCheck = min;
        }
        if(toCheck > max)
        {
            toCheck = max;
        }
        registry.put(bits[0], toCheck);
    }

    @Override
    public String[] getName()
    {
        return new String[] {"bound"};
    }

}
