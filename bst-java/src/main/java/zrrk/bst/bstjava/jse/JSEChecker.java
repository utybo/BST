/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.jse;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.ScriptChecker;
import zrrk.bst.bstjava.api.script.VariableRegistry;
import zrrk.bst.bstjava.api.story.BranchingStory;

public class JSEChecker implements ScriptChecker
{

    @Override
    public boolean check(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        final VariableRegistry registry = story.getRegistry();
        if(!client.getHTBHandler().requestJSAccess())
            throw new BSTException(line, "Javascript access denied", story);
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        JSEAction.applyReg(engine, registry, line, story);
        try
        {
            final Object result = engine.eval(desc);
            if(result instanceof Boolean)
            {
                return (Boolean)result;
            }
            else if(result instanceof Number)
            {
                final int i = ((Number)result).intValue();
                if(i <= 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else if(result == null)
            {
                throw new BSTException(line, "No returned value", story);
            }
            else
            {
                throw new BSTException(line, "Unknown value type : " + result.getClass().getName(),
                        story);
            }
        }
        catch(final ScriptException e)
        {
            throw new BSTException(line, "Error during script execution : " + e.getMessage(), e,
                    story);
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"jse_eval", "js"};
    }

}
