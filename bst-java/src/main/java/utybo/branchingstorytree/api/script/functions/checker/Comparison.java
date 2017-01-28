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
 * Implementation of some comparison checkers : greater, greaterequ, less and
 * lessequ
 * 
 * @author utybo
 *
 */
public class Comparison implements ScriptChecker
{

    @Override
    public boolean check(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
        final String varName = desc.split(",")[0];
        final Integer var = (Integer)registry.get(varName, 0);
        final String compareTo = desc.split(",")[1];
        final Integer var2 = registry.typeOf(compareTo) == Integer.class ? (Integer)registry.get(compareTo, 0) : Integer.parseInt(compareTo);

        switch(head)
        {
        case "greater":
            return var > var2;
        case "greaterequ":
            return var >= var2;
        case "less":
            return var < var2;
        case "lessequ":
            return var <= var2;
        default:
            throw new BSTException(line, "Internal error");
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"greater", "greaterequ", "less", "lessequ"};
    }

}
