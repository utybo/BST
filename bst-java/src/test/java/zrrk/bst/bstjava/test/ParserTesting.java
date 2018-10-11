/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.BranchingStoryTreeParser;
import zrrk.bst.bstjava.api.script.Dictionary;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.LogicalNode;
import zrrk.bst.bstjava.api.story.TextNode;
import zrrk.bst.bstjava.api.story.VirtualNode;

public class ParserTesting
{
    @Test
    public void parserFullTest()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        final BranchingStory s = testFile("parser.bst", null);
        assertTrue(s.getInitialNode().getId() == 1);
        final TextNode node1 = (TextNode)s.getNode(1);
        assertTrue(node1.getText().equals("Example\n\nof\n\nsome\nnode"));
        assertTrue(node1.getOptions().size() == 4);
        final VirtualNode node2 = (VirtualNode)s.getNode(2);
        assertTrue(node2.getText().equals("Wheee!"));
        final LogicalNode node3 = (LogicalNode)s.getNode(3);
        assertTrue(node3.solve(s).getId() == 56);
    }

    public static BranchingStory testFile(final String path, final BSTClient client)
            throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        final Dictionary d = new Dictionary();
        return new BranchingStoryTreeParser().parse(
                new BufferedReader(new InputStreamReader(ActionTesting.class
                        .getResourceAsStream("/utybo/branchingstorytree/api/test/files/" + path))),
                d, client, path);
    }
}
