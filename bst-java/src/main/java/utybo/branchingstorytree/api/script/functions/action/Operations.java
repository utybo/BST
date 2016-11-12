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

public class Operations implements ScriptAction
{

    @Override
    public void exec(String head, String desc, VariableRegistry registry, BSTClient client) throws BSTException
    {
        final String[] pars = desc.split(",");
        String putIn = null;
        String a = null;
        String b = null;
        if(pars.length == 2)
        {
            putIn = a = pars[0];
            b = pars[1];
        }
        else if(pars.length == 3)
        {
            putIn = pars[0];
            a = pars[1];
            b = pars[2];
        }
        else
        {
            throw new BSTException(-1, "Invalid syntax : {" + head + ":a,b} for a + b with result in a or {add:a,b,c} for b + c with result in a");
        }

        final int ia = registry.typeOf(a) == Integer.class ? (Integer)registry.get(a, 0) : Integer.parseInt(a);
        final int ib = registry.typeOf(b) == Integer.class ? (Integer)registry.get(b, 0) : Integer.parseInt(b);

        switch(head)
        {
        case "add":
            registry.put(putIn, ia + ib);
            break;
        case "sub":
            registry.put(putIn, ia - ib);
            break;
        case "mul":
            registry.put(putIn, ia * ib);
            break;
        case "div":
            registry.put(putIn, ia / ib);
            break;
        case "mod":
            registry.put(putIn, ia % ib);
        }

    }

    @Override
    public String[] getName()
    {
        return new String[] {"add", "sub", "mul", "div"};
    }

}
