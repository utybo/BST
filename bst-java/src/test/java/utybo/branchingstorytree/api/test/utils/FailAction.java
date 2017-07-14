/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test.utils;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

public class FailAction implements ScriptAction
{
    @Override
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client) throws BSTException
    {
        throw new BSTException(-1, desc, story);
    }

    @Override
    public String[] getName()
    {
        return new String[]{"fail", "crash"};
    }

}
