/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script.functions.action;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.ScriptAction;
import zrrk.bst.bstjava.api.script.VariableRegistry;
import zrrk.bst.bstjava.api.story.BranchingStory;

/**
 * Implementation of operation related actions : add (addition), sub
 * (substraction), mul (multiplication), div (division), mod (modulo)
 *
 * @author utybo
 *
 */
public class Operations implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
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
            throw new BSTException(line, "Invalid syntax : {" + head
                    + ":a,b} for a + b with result in a or {add:a,b,c} for b + c with result in a",
                    story);
        }

        int ia, ib;
        try
        {
            ia = registry.typeOf(a) == Integer.class ? (Integer)registry.get(a, 0)
                    : Integer.parseInt(a);
        }
        catch(final NumberFormatException nfe)
        {
            // This means that the first number is probably a variable that was not initialized
            // thus registry.typeOf(a) returned null. We take 0 by default.
            ia = 0;
        }
        try
        {
            ib = registry.typeOf(b) == Integer.class ? (Integer)registry.get(b, 0)
                    : Integer.parseInt(b);
        }
        catch(final NumberFormatException nfe)
        {
            ib = 0;
        }

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
            break;
        default:
            // Cannot happen
            break;
        }

    }

    @Override
    public String[] getName()
    {
        return new String[] {"add", "sub", "mul", "div", "mod"};
    }

}
