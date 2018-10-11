/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.xsf;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.ScriptAction;
import zrrk.bst.bstjava.api.script.VariableRegistry;
import zrrk.bst.bstjava.api.story.BranchingStory;

public class XSFAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client)
            throws BSTException
    {
        XSFHandler xsf = client.getXSFHandler();
        if(xsf == null)
            throw new BSTException(line, "XSF not supported", story);
        switch(head)
        {
        case "xsf_exec":
        case "jsf":
        {
            String[] bits = desc.split(",");
            if(bits.length < 2 || bits.length > 3)
            {
                throw new BSTException(line,
                        "Incorrect syntax : xsf_exec:filename,function OR xsf_exec:filename,function,putIn",
                        story);
            }
            if(!client.getHTBHandler().requestJSAccess())
                throw new BSTException(line, "Javascript access denied", story);
            Object ret = xsf.invokeScript(bits[0], bits[1], new XSFBridge(story, client, line),
                    story, line);
            if(bits.length == 3 && ret != null)
                convertAndSaveVariable(bits[2], ret, story.getRegistry());
            break;
        }
        case "xsf_createeng":
        {
            String[] bits = desc.split(",");
            int eng;
            if(bits[0].isEmpty())
                eng = 0;
            else
            {
                VariableRegistry vr = story.getRegistry();
                if(vr.typeOf(desc) == Integer.class)
                    eng = (Integer)vr.get(bits[0], 0);
                else
                    try
                    {
                        eng = Integer.parseInt(bits[0]);
                    }
                    catch(NumberFormatException nfe)
                    {
                        throw new BSTException(line,
                                "Incorrect syntax : xsf_createeng:integer OR xsf_createeng:integer,toLoad,toLoad...",
                                story);
                    }
            }
            if(bits.length > 1)
            {
                String[] filesToLoad = new String[bits.length - 1];
                System.arraycopy(bits, 1, filesToLoad, 0, filesToLoad.length);
                xsf.createEngine(story, line, eng, filesToLoad);
            }
            else
                xsf.createEngine(story, line, eng);
            break;
        }
        case "xsf_execeng":
        case "jsfe":
        {
            String[] bits = desc.split(",");

            if(bits.length < 3)
            {
                throw new BSTException(line,
                        "Incorrect syntax : xsf_execeng:engine,putIn,javascript", story);
            }

            int i = 0;
            try
            {
                i = Integer.parseInt(bits[0]);
            }
            catch(NumberFormatException nfe)
            {
                throw new BSTException(line,
                        "Incorrect syntax : xsf_execeng:engine,putIn,javascript", story);
            }
            if(!client.getHTBHandler().requestJSAccess())
                throw new BSTException(line, "Javascript access denied", story);
            Object ret = xsf.invokeScriptInEngine(i, bits[2], new XSFBridge(story, client, line),
                    story, line);
            if(!bits[1].isEmpty())
            {
                convertAndSaveVariable(bits[1], ret, story.getRegistry());
            }
            break;
        }
        case "xsf_importvar":
        {
            int i;
            try
            {
                i = Integer.parseInt(desc);
            }
            catch(NumberFormatException e)
            {
                throw new BSTException(line, "Incorrect syntax : xsf_importvar:integer", story);
            }

            xsf.importAllVariables(i, story, line);
        }
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"xsf_exec", "jsf", "xsf_createeng", "xsf_execeng", "jsfe",
                "xsf_importvar"};
    }

    protected static void convertAndSaveVariable(String varName, Object obj, VariableRegistry reg)
    {
        if(obj instanceof Number)
            reg.put(varName, ((Number)obj).intValue());
        else if(obj instanceof Boolean)
            reg.put(varName, ((Boolean)obj) ? 1 : 0);
        else
            reg.put(varName, obj.toString());
    }

}
