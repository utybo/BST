/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.xsf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

public class XSFAction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client)
            throws BSTException
    {
        String[] bits = desc.split(",");
        XSFHandler xsf = client.getXSFHandler();
        if(bits.length < 2 || bits.length > 3)
        {
            throw new BSTException(line,
                    "Incorrect syntax : xsf_exec:filename,function OR xsf_exec:filename,function,putIn",
                    story);
        }
        if(xsf == null)
            throw new BSTException(line, "XSF is not supported.", story);
        if(!client.getHTBHandler().requestJSAccess())
            throw new BSTException(line, "Javascript access denied", story);
        Object ret = xsf.invokeScript(bits[0], bits[1], new XSFBridge(story, client, line), story,
                line);
        if(bits.length == 3 && ret != null)
            convertAndSaveVariable(bits[2], ret, story.getRegistry());
    }

    @Override
    public String[] getName()
    {
        return new String[] {"xsf_exec"};
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
