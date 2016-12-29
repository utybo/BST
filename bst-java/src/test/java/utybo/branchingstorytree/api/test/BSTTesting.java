/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;

public class BSTTesting
{
    @Test
    public void testOperations() throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("operations.bst");
    }
    
    @Test
    public void testCheckers() throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("set.bst");
    }
    
    public static void testFile(String path) throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        Dictionnary d = new Dictionnary();
        BranchingStory story = new BranchingStoryTreeParser().parse(new BufferedReader(new InputStreamReader(BSTTesting.class.getResourceAsStream("/utybo/branchingstorytree/api/test/files/" + path ))), d, null);
        StoryNode node = story.getInitialNode();
        while(node != null)
        {
            if(node instanceof LogicalNode)
                node = story.getNode(((LogicalNode)node).solve());
            else
                throw new BSTException(-1, node.getId() + " isn't a logical node");
        }
    }
}
