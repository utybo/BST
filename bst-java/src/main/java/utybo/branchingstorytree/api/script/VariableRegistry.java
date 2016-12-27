/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import java.util.HashMap;

/**
 * A registry of all the variables. The contract is that there cannot be
 * multiple variables with the same name.
 * 
 * @author utybo
 *
 */
public class VariableRegistry
{
    private final HashMap<String, Integer> variables = new HashMap<>();
    private final HashMap<String, String> strVar = new HashMap<>();

    public void put(final String name, final int var)
    {
        remove(name);
        variables.put(name, var);
    }

    public void put(final String name, final String value)
    {
        remove(name);
        strVar.put(name, value);
    }

    public void remove(final String name)
    {
        variables.remove(name);
        strVar.remove(name);
    }

    @Deprecated
    public int getInt(final String name)
    {
        return variables.getOrDefault(name, 0);
    }

    public HashMap<String, Integer> getAllInt()
    {
        return variables;
    }

    public HashMap<String, String> getAllString()
    {
        return strVar;
    }

    public void reset()
    {
        variables.clear();
        strVar.clear();
    }

    public Class<?> typeOf(final String name)
    {
        if(variables.containsKey(name))
        {
            return Integer.class;
        }

        return null;
    }

    public Object get(final String varName, final Object ifNotFound)
    {
        Object tryingToFindMe = null;
        if(variables.containsKey(varName))
        {
            tryingToFindMe = variables.get(varName);
        }
        else if(strVar.containsKey(varName))
        {
            tryingToFindMe = strVar.get(varName);
        }
        else
        {
            tryingToFindMe = ifNotFound;
        }
        return tryingToFindMe;
    }

    @Override
    public VariableRegistry clone()
    {
        final VariableRegistry vr = new VariableRegistry();
        vr.strVar.putAll(strVar);
        vr.variables.putAll(variables);
        return vr;
    }

    public int getSize()
    {
        return variables.size() + strVar.size();
    }
}
