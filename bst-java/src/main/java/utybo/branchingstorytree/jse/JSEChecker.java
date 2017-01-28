/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.jse;

import static utybo.branchingstorytree.jse.JSEAction.checkReg;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptChecker;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

public class JSEChecker implements ScriptChecker
{

    @Override
    public boolean check(String head, String desc, int line, BranchingStory story, BSTClient client) throws BSTException
    {
        JSEHandler handler = client.getJSEHandler();
        VariableRegistry registry = story.getRegistry();
        if(handler.getEngine() == null || !registry.get("__jse__auto", "true").toString().equalsIgnoreCase("false"))
        {
            handler.setEngine(new ScriptEngineManager().getEngineByName("JavaScript"));
        }
        ScriptEngine engine = handler.getEngine();
        checkReg(engine, registry, line);
        try
        {
            Object result = engine.eval(desc);
            if(result instanceof Boolean)
            {
                return (Boolean)result;
            }
            else if(result instanceof Number)
            {
                int i = ((Number)result).intValue();
                System.out.println(desc + " ==> " + i);
                if(i <= 0)
                {
                    return false;
                }
                else
                    return true;
            }
            else if(result == null)
            {
                throw new BSTException(line, "No returned value");
            }
            else
            {
                throw new BSTException(line, "Unknown value type : " + result.getClass().getName());
            }
        }
        catch(ScriptException e)
        {
            throw new BSTException(line, "Error during script execution : " + e.getMessage(), e);
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"jse_eval"};
    }

}
