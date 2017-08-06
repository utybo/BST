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

import javax.script.ScriptEngineManager;

import org.junit.Before;
import org.junit.Test;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.test.utils.JSETestClient;

public class ActionTesting
{
    @Before
    public void init()
    {
        new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @Test
    public void testJse()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("jse.bst", new JSETestClient());
    }

    @Test
    public void testFastJse()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("jse_fast.bst", new JSETestClient());
    }

    @Test
    public void testOperations()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("operations.bst", null);
    }

    @Test
    public void testCheckers()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("set.bst", null);
    }

    @Test
    public void testCall()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("call.bst", null);
    }

    @Test
    public void testBound()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("bound.bst", null);
    }

    @Test
    public void testRand()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("rand.bst", null);
    }

    @Test
    public void testClone()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("clone.bst", null);
    }

    @Test
    public void testIncrDecr()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("incrdecr.bst", null);
    }

    public static BranchingStory testFile(final String path, final BSTClient client)
            throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        final Dictionnary d = new Dictionnary();
        final BranchingStory story = new BranchingStoryTreeParser().parse(
                new BufferedReader(new InputStreamReader(ActionTesting.class
                        .getResourceAsStream("/utybo/branchingstorytree/api/test/files/" + path))),
                d, client, path);
        StoryNode node = story.getInitialNode();
        while(node != null)
        {
            if(node instanceof LogicalNode)
            {
                node = ((LogicalNode)node).solve(story);
            }
            else
            {
                throw new BSTException(-1, node.getId() + " isn't a logical node", story);
            }
        }
        return story;
    }
}
