/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;

public class AliasAndAutoIDTesting
{
    @Test
    public void aliasAndAutoIDTest() throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        BranchingStory bs = ActionTesting.testFile("aliasautoid.bst", null);
        Assert.assertEquals(bs.getRegistry().get("a", -1), 3);
    }
}
