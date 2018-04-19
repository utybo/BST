/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.bdf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * Implementation of BDF's bdf_apply
 *
 * @author utybo
 *
 */
public class BDFAction implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        final BDFHandler bdf = client.getBDFHandler();
        if(bdf == null)
        {
            throw new BSTException(line, "BDF not supported", story);
        }
        final String[] bits = desc.split(",");
        if(bits.length > 2)
        {
            throw new BSTException(line, "Invalid syntax : bdf_apply:name OR bdf_apply:name,prefix",
                    story);
        }
        String name = bits[0];
        if(name.startsWith("!"))
        {
            name = story.getRegistry().get(name.substring(1), "").toString();
        }
        final String prefix = bits.length == 2 ? bits[1] : "";
        if(bdf.getBDFFile(name) == null)
        {
            throw new BSTException(line, "Unknown BDF file : " + name, story);
        }
        bdf.getBDFFile(name).applyTo(story.getRegistry(), prefix);
    }

    @Override
    public String[] getName()
    {
        return new String[] {"bdf_apply"};
    }

}
