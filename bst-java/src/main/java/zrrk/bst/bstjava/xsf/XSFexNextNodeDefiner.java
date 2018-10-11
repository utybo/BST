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
import zrrk.bst.bstjava.api.Experimental;
import zrrk.bst.bstjava.api.NodeNotFoundException;
import zrrk.bst.bstjava.api.script.NextNodeDefiner;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.StoryNode;
import zrrk.bst.bstjava.api.story.VirtualNode;

@Experimental
public class XSFexNextNodeDefiner implements NextNodeDefiner
{
    public final String desc;
    private final int line;
    private final BSTClient client;

    public XSFexNextNodeDefiner(String head, String desc, int line, BSTClient client)
    {
        this.line = line;
        this.desc = desc;
        this.client = client;
    }

    @Override
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException
    {
        // TODO finish
        // run the script like the xsf action, then check for the output
        // - if it's a story node, that's our next node
        // - if it's anything else that is not null, convert to String and put that
        //   in a virtual node, that's our next node
        // - if it's null, return an empty virtual node

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
        Object ret = xsf.invokeScript(bits[0], bits[1], new XSFBridge(story, client, line), story,
                line);
        if(ret instanceof StoryNode)
            return (StoryNode)ret;
        VirtualNode vn = new VirtualNode(-1, null);
        if(ret != null)
        {
            vn.setText(ret.toString());
        }
        return vn;
    }

}
