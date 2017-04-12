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
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final JSEHandler handler = client.getJSEHandler();

        if(head.equals("jse_reset"))
        {
            handler.setEngine(null);
            return;
        }

        final VariableRegistry registry = story.getRegistry();

        if(handler.getEngine() == null || !registry.get("__jse__auto", "true").toString().equalsIgnoreCase("false"))
        {
            handler.setEngine(new ScriptEngineManager().getEngineByName("JavaScript"));
        }

        final ScriptEngine engine = handler.getEngine();
        if(engine == null)
        {
            throw new Error("Well this doesn't make any sense");
        }
        switch(head)
        {
        case "jse_eval":
        {
            checkReg(engine, registry, line, story);

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
                    System.err.println("[line " + line + "] Unknown return type : " + result.getClass().getName() + ". Using toString!");
                    registry.put(varName, result.toString());
                }
            }
            catch(final ScriptException e1)
            {
                throw new BSTException(line, "Error during script execution : " + e1.getMessage(), e1, story.getTag("__sourcename"));
            }
            break;
        }
        case "jse_import":
        {
            try
            {
                for(final String varName : desc.split(","))
                {
                    final Class<?> type = registry.typeOf(varName);

                    if(type.equals(Integer.class))
                    {
                        engine.eval(varName + " = " + registry.get(varName, ""));
                    }
                    else if(type.equals(String.class))
                    {
                        engine.eval(varName + " = \"" + registry.get(varName, ""));
                    }
                    else
                    {
                        throw new BSTException(line, "Unknown variable : " + varName);
                    }

                }
            }
            catch(final ScriptException e)
            {
                throw new BSTException(line, "Internal error", e, story.getTag("__sourcename"));
            }
            break;
        }
        case "jse_autoimport":
            registry.put("__jse__auto", desc);
        }

    }

    @Override
    public String[] getName()
    {
        return new String[] {"jse_eval", "jse_reset", "jse_autoimport", "jse_import"};
    }

    public static void checkReg(final ScriptEngine engine, final VariableRegistry registry, final int line, BranchingStory story) throws BSTException
    {
        if(!registry.get("__jse__auto", "true").toString().equalsIgnoreCase("false"))
        {
            final HashMap<String, Integer> ints = registry.getAllInt();
            for(final String name : ints.keySet())
            {
                try
                {
                    engine.eval(name + " = " + ints.get(name));
                }
                catch(final ScriptException e1)
                {
                    throw new BSTException(line, "Error during JSE initialization (step INT) : " + e1.getMessage(), e1, story.getTag("__sourcename"));
                }
            }
            final HashMap<String, String> strings = registry.getAllString();
            for(final String name : strings.keySet())
            {
                try
                {
                    engine.eval(name + " = \"" + strings.get(name) + "\"");
                }
                catch(final ScriptException e1)
                {
                    throw new BSTException(line, "Error during JSE initialization (step STRING) : " + e1.getMessage(), e1, story.getTag("__sourcename"));
                }
            }
        }
    }
}
