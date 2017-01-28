/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.script.StaticNextNode;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.VirtualNode;
import utybo.branchingstorytree.api.story.logicalnode.LNCondReturn;

public class StoryUtilsTesting
{
    @Test
    public void testVarSolve() throws BSTException
    {
        BranchingStory story = new BranchingStory();
        VariableRegistry vars = story.getRegistry();
        vars.put("test", 42);
        String testDrive = "${test} ${>4} ${&5}";
        // Prepare two nodes for testing
        String testNode = "Test Complete";
        VirtualNode vn = new VirtualNode(4);
        vn.setText(testNode);
        story.addNode(vn);
        LogicalNode ln = new LogicalNode(5);
        story.addNode(ln);
        ln.addInstruction(new LNCondReturn(new StaticNextNode(4)));
        // Perform test
        VirtualNode vn2 = new VirtualNode(555);
        vn2.setText(testDrive);
        String s = StoryUtils.solveVariables(vn2, story);
        assertEquals(s, "42 Test Complete Test Complete");
    }
}
