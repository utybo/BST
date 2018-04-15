/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.jse;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * Implementation of the JSE module's action
 *
 * @author utybo
 *
 */
public class JSEAction implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();

        if(!client.getHTBHandler().requestJSAccess())
            throw new BSTException(line, "Javascript access denied", story);

        switch(head)
        {
        case "jse_eval":
        {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            applyReg(engine, registry, line, story);

            // Parse
            final String varName = desc.split(",")[0];
            final String script = desc.substring(desc.indexOf(',') + 1);

            // Exec
            try
            {
                final Object result = engine.eval(script);
                if(result instanceof Number)
                {
                    registry.put(varName, ((Number)result).intValue());
                }
                else if(result instanceof String)
                {
                    registry.put(varName, (String)result);
                }
                else if(result == null)
                {
                    registry.remove(varName);
                }
                else
                {
                    client.warn("[line " + line + "] Unknown return type : "
                            + result.getClass().getName() + ". Using toString!");
                    registry.put(varName, result.toString());
                }
            }
            catch(final ScriptException e1)
            {
                throw new BSTException(line, "Error during script execution : " + e1.getMessage(),
                        e1, story);
            }
            break;
        }
        default:
            // Happens for the now deprecated manual import mechanism
            break;
        }

    }

    @Override
    public String[] getName()
    {
        return new String[] {"jse_eval", "jse_reset", "jse_autoimport", "jse_import"};
    }

    protected static void applyReg(final ScriptEngine engine, final VariableRegistry registry,
            final int line, BranchingStory story) throws BSTException
    {
        final HashMap<String, Integer> ints = registry.getAllInt();
        for(final Map.Entry<String, Integer> entry : ints.entrySet())
        {
            try
            {
                engine.eval(entry.getKey() + " = " + entry.getValue());
            }
            catch(final ScriptException e1)
            {
                throw new BSTException(line,
                        "Error during JSE initialization (step INT) : " + e1.getMessage(), e1,
                        story);
            }
        }
        final HashMap<String, String> strings = registry.getAllString();
        for(final Map.Entry<String, String> entry : strings.entrySet())
        {
            try
            {
                engine.eval(entry.getKey() + " = \"" + entry.getValue() + "\"");
            }
            catch(final ScriptException e1)
            {
                throw new BSTException(line,
                        "Error during JSE initialization (step STRING) : " + e1.getMessage(), e1,
                        story);
            }
        }
    }
}
