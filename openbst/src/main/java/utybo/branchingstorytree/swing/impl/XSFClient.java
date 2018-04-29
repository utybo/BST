/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.xsf.XSFBridge;
import utybo.branchingstorytree.xsf.XSFHandler;

public class XSFClient implements XSFHandler
{
    private HashMap<String, String> scripts = new HashMap<>();
    private HashMap<Integer, ScriptEngine> engines = new HashMap<>();

    @Override
    public void load(InputStream in, String name) throws BSTException
    {
        try
        {
            scripts.put(name, IOUtils.toString(in, StandardCharsets.UTF_8));
        }
        catch(IOException e)
        {
            throw new BSTException(-1, "Unexpected IO Exception during XSF loading", e, "<none>");
        }
    }

    @Override
    public Object invokeScript(String resourceName, String function, XSFBridge bst,
            BranchingStory story, int line) throws BSTException
    {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
        Bindings binds = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.putAll(story.getRegistry().getAllInt());
        binds.putAll(story.getRegistry().getAllString());
        binds.put("bst", bst);
        try
        {
            scriptEngine.eval(scripts.get(resourceName));
            return scriptEngine.eval(function + "()");
        }
        catch(ScriptException e)
        {
            throw new BSTException(line, "Script exception : " + e.getMessage(), story);
        }
    }

    @Override
    public void createEngine(BranchingStory story, int line, int eng, String... toLoad)
            throws BSTException
    {
        story.getRegistry().put("__savestate_warning", 1);
        ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");
        if(toLoad.length > 0)
        {
            if("ALL".equals(toLoad[0]))
            {
                for(String s : scripts.values())
                {
                    try
                    {
                        se.eval(s);
                    }
                    catch(ScriptException e)
                    {
                        throw new BSTException(line, "Script exception while evaluating "+s+ " : " + e.getMessage(), story);
                    }
                }
            }
            else
            {
                for(String s : toLoad)
                {
                    try
                    {
                        String str = scripts.get(s);
                        if(str == null)
                            throw new BSTException(line, "Unknown XSF file : " + str, story);
                        se.eval(str);
                    }
                    catch(ScriptException e)
                    {
                        throw new BSTException(line, "Script exception while evaluating "+s+ " : " + e.getMessage(), story);
                    }
                }
            }
        }
        engines.put(eng, se);
    }

    @Override
    public Object invokeScriptInEngine(int engine, String string2, XSFBridge xsfBridge,
            BranchingStory story, int line) throws BSTException
    {
        ScriptEngine scriptEngine = engines.get(engine);
        if(scriptEngine == null)
            throw new BSTException(line, "No engine for slot " + engine, story);
        Bindings binds = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.put("bst", xsfBridge);
        try
        {
            return scriptEngine.eval(string2);
        }
        catch(ScriptException e)
        {
            throw new BSTException(line, "Script exception : " + e.getMessage(), story);
        }
    }

    @Override
    public void importAllVariables(int i, BranchingStory story, int line) throws BSTException
    {
        ScriptEngine scriptEngine = engines.get(i);
        if(scriptEngine == null)
            throw new BSTException(line, "No engine for slot " + i, story);
        Bindings binds = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.putAll(story.getRegistry().getAllInt());
        binds.putAll(story.getRegistry().getAllString());
    }

}
