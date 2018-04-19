/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script.functions.checker;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptChecker;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * Implementation of the equ and not checkers
 *
 * @author utybo
 *
 */
public class EqualityInequality implements ScriptChecker
{

    @Override
    public boolean check(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
        boolean equ;
        switch(head)
        {
        case "equ":
            equ = true;
            break;
        case "not":
            equ = false;
            break;
        default:
            throw new BSTException(line, "Internal error", story);
        }
        if(desc.split(",").length != 2)
        {
            throw new BSTException(line, "You need two arguments with any operation", story);
        }
        final String varName = desc.split(",")[0];
        final Object var = registry.get(varName, 0);
        final String isEqualWith = desc.split(",")[1];

        try
        {
            if(registry.typeOf(isEqualWith) != null)
            {
                return var.toString().equals(registry.get(isEqualWith, 0).toString());
            }
            final int i = Integer.parseInt(isEqualWith);
            if(var.getClass() == Integer.class)
            {

                return ((Integer)var).intValue() == i == equ;
            }
        }
        catch(final NumberFormatException e)
        {}
        return var.toString().equals(isEqualWith) == equ;
    }

    @Override
    public String[] getName()
    {
        return new String[] {"equ", "not"};
    }

}
