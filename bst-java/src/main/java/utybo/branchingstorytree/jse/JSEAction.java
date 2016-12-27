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
        // Initialize the engine
        final ScriptEngine e = new ScriptEngineManager().getEngineByName("JavaScript");
        final VariableRegistry registry = story.getRegistry();
        final HashMap<String, Integer> ints = registry.getAllInt();
        for(final String name : ints.keySet())
        {
            try
            {
                e.eval(name + " = " + ints.get(name));
            }
            catch(final ScriptException e1)
            {
                throw new BSTException(line, "Error during JSE initialization (step INT)", e1);
            }
        }
        final HashMap<String, String> strings = registry.getAllString();
        for(final String name : strings.keySet())
        {
            try
            {
                e.eval(name + " = \"" + strings.get(name) + "\"");
            }
            catch(final ScriptException e1)
            {
                throw new BSTException(line, "Error during JSE initialization (step STRING)", e1);
            }
        }

        // Parse
        final String varName = desc.split(",")[0];
        final String script = desc.substring(desc.indexOf(',') + 1);

        // Exec
        try
        {
            final Object result = e.eval(script);
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
            throw new BSTException(line, "Error during script execution : " + e1.getMessage(), e1);
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"jse_eval"};
    }

}
