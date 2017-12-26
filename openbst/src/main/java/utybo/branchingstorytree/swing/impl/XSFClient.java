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

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.xsf.XSFBridge;
import utybo.branchingstorytree.xsf.XSFHandler;

public class XSFClient implements XSFHandler
{
    private HashMap<String, String> scripts = new HashMap<>();

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
        SimpleBindings binds = new SimpleBindings();
        binds.putAll(story.getRegistry().getAllInt());
        binds.putAll(story.getRegistry().getAllString());
        binds.put("bst", bst);
        scriptEngine.setBindings(binds, ScriptContext.ENGINE_SCOPE);
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

}
