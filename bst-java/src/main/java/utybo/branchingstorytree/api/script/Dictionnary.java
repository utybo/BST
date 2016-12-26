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

import org.atteo.classindex.ClassIndex;

import utybo.branchingstorytree.api.BSTException;

public class Dictionnary
{
    private final HashMap<String, ScriptAction> actions = new HashMap<>();
    private final HashMap<String, ScriptChecker> checkers = new HashMap<>();

    public Dictionnary() throws InstantiationException, IllegalAccessException
    {
        for(final Class<? extends ScriptAction> jclass : ClassIndex.getSubclasses(ScriptAction.class))
        {
            final ScriptAction sa = jclass.newInstance();
            final String[] names = sa.getName();
            if(names != null)
            {
                for(final String s : names)
                {
                    actions.put(s, sa);
                }
            }
        }
        for(final Class<? extends ScriptChecker> jclass : ClassIndex.getSubclasses(ScriptChecker.class))
        {
            final ScriptChecker sa = jclass.newInstance();
            final String[] names = sa.getName();
            if(names != null)
            {
                for(final String s : names)
                {
                    checkers.put(s, sa);
                }
            }
        }

    }

    public ScriptAction getAction(final String action) throws BSTException
    {
        return actions.get(action);
    }

    public ScriptChecker getChecker(final String checker) throws BSTException
    {
        return checkers.get(checker);
    }
}
