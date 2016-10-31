/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import javax.swing.JOptionPane;

import utybo.branchingstorytree.api.BSTCentral;
import utybo.branchingstorytree.api.BSTException;

public class Dictionnary
{
    public ScriptAction getAction(final String action, final String desc, final VariableRegistry registry) throws BSTException
    {
        switch(action)
        {
        // TODO Handle class cast exceptions
        case "incr":
            return () ->
            {
                if(registry.typeOf(desc) != Integer.class)
                {
                    throw new BSTException(-1, "incr : The variable " + desc + " is not a number.");
                }
                registry.put(desc, (Integer)registry.get(desc) + 1);

            };
        case "decr":
            return () -> registry.put(desc, (Integer)registry.get(desc) - 1);
        case "set":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final String value = desc.substring(desc.indexOf(',') + 1);
                try
                {
                    registry.put(varName, Integer.parseInt(value));
                }
                catch(final NumberFormatException e)
                {
                    // No printStackTrace because this exception is expected in many cases
                    registry.put(varName, value);

                }
            };
        case "add":
            return () ->
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
                    throw new BSTException(-1, "Invalid syntax : {add:a,b} for a + b with result in a or {add:a,b,c} for b + c with result in a");
                }

                final int ia = registry.typeOf(a) == Integer.class ? (Integer)registry.get(a) : Integer.parseInt(a);
                final int ib = registry.typeOf(b) == Integer.class ? (Integer)registry.get(b) : Integer.parseInt(b);
                registry.put(putIn, ia + ib);

            };
        case "exit":
            return () -> System.exit(0);
        case "input":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final String msg = desc.substring(desc.indexOf(',') + 1);
                String input = null;
                while(input == null || input.isEmpty())
                {
                    input = JOptionPane.showInputDialog(BSTCentral.getPlayerComponent(), msg);
                }
                registry.put(varName, input);
            };
        default:
            return null;
        }
    }

    public ScriptChecker getChecker(final String action, final String desc, final VariableRegistry registry) throws BSTException
    {
        switch(action)
        {
        // TODO Handle class cast exceptions + string comparison
        case "equ":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final Object var = registry.get(varName);
                final String isEqualWith = desc.split(",")[1];

                try
                {
                    final int i = Integer.valueOf(isEqualWith);
                    if(var.getClass() == Integer.class)
                    {
                        return ((Integer)var).intValue() == i;
                    }
                }
                catch(final NumberFormatException e)
                {}
                return var.toString().equals(isEqualWith);
            };
        case "not":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final Object var = registry.get(varName);
                final String isEqualWith = desc.split(",")[1];

                try
                {
                    final int i = Integer.valueOf(isEqualWith);
                    if(var.getClass() == Integer.class)
                    {
                        return ((Integer)var).intValue() != i;
                    }
                }
                catch(final NumberFormatException e)
                {}
                return !var.toString().equals(isEqualWith);
            };
        case "more":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final Integer var = (Integer)registry.get(varName);
                final Integer compareTo = Integer.parseInt(desc.split(",")[1]);

                return var > compareTo;
            };
        case "less":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final Integer var = (Integer)registry.get(varName);
                final Integer compareTo = Integer.parseInt(desc.split(",")[1]);

                return var < compareTo;
            };
        case "moreequ":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final Integer var = (Integer)registry.get(varName);
                final Integer compareTo = Integer.parseInt(desc.split(",")[1]);

                return var >= compareTo;
            };
        case "lessequ":
            return () ->
            {
                final String varName = desc.split(",")[0];
                final Integer var = (Integer)registry.get(varName);
                final Integer compareTo = Integer.parseInt(desc.split(",")[1]);

                return var <= compareTo;
            };
        default:
            return null;
        }
    }
}
