/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script.functions.action;

import java.util.Random;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * Implementation of the rand action
 * 
 * @author utybo
 *
 */
public class RandomAction implements ScriptAction
{
    @Override
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
        final String[] bits = desc.split(",");
        int min = 0;
        int max;
        String varToSet;
        if(bits.length == 2)
        {
            varToSet = bits[0];
            max = registry.typeOf(bits[1]) == Integer.class ? (Integer)registry.get(bits[1], 0) : Integer.parseInt(bits[1]);
        }
        else if(bits.length == 3)
        {
            varToSet = bits[0];
            min = registry.typeOf(bits[1]) == Integer.class ? (Integer)registry.get(bits[1], 0) : Integer.parseInt(bits[1]);
            max = registry.typeOf(bits[2]) == Integer.class ? (Integer)registry.get(bits[2], 0) : Integer.parseInt(bits[2]);
        }
        else
        {
            throw new BSTException(line, "Incorrect syntax : rand:variabletoset,maximum OR rand:variabletoset,minimum,maximum");
        }
        // nextInt is exclusive, we make it "inclusive" by adding 1
        int value = new Random().nextInt(max + 1);
        // Minimum bounds
        value += min;
        registry.put(varToSet, value);
    }

    @Override
    public String[] getName()
    {
        return new String[] {"rand"};
    }
}
