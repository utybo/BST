/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.brm;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * Implementation of actions related to BRM
 * 
 * @author utybo
 *
 */
public class BRMAction implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final BRMHandler brm = client.getBRMHandler();
        if(brm == null)
        {
            throw new BSTException(line, "brm not supported");
        }
        brm.loadAuto();
    }

    @Override
    public String[] getName()
    {
        return new String[] {"brm_load"};
    }

}
