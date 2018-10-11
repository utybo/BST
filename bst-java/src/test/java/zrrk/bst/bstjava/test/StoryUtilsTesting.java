/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.StoryUtils;
import zrrk.bst.bstjava.api.script.SimpleNextNodeDefiner;
import zrrk.bst.bstjava.api.script.VariableRegistry;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.LogicalNode;
import zrrk.bst.bstjava.api.story.VirtualNode;
import zrrk.bst.bstjava.api.story.logicalnode.LNCondReturn;

public class StoryUtilsTesting
{
    @Test
    public void testVarSolve() throws BSTException
    {
        final BranchingStory story = new BranchingStory();
        final VariableRegistry vars = story.getRegistry();
        vars.put("test", 42);
        final String testDrive = "${test} ${>4} ${&5}";
        // Prepare two nodes for testing
        final String testNode = "Test Complete";
        final VirtualNode vn = new VirtualNode(4, story);
        vn.setText(testNode);
        story.addNode(vn);
        final LogicalNode ln = new LogicalNode(5, story);
        story.addNode(ln);
        ln.addInstruction(new LNCondReturn(new SimpleNextNodeDefiner("4")));
        // Perform test
        final VirtualNode vn2 = new VirtualNode(555, story);
        vn2.setText(testDrive);
        final String s = StoryUtils.solveVariables(vn2, story);
        assertEquals(s, "42 Test Complete Test Complete");
    }
}
