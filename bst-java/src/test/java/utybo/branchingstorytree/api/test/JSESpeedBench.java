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

public class JSESpeedBench
{
    @Before
    public void init()
    {
        // Just make sure we have the JavaScript libraries ready
        // to operate, as this can slow the process down considerably
        new ScriptEngineManager().getEngineByName("JavaScript");
    }
    @Test
    public void nativeSpeedTest() throws Exception
    {
        testFile("native.bst", new JSETestClient());
    }
    
    @Test
    public void jseSpeedTest() throws Exception
    {
        testFile("jse.bst", new JSETestClient());
    }
    
    public static void testFile(String path, BSTClient client) throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        Dictionnary d = new Dictionnary();
        BranchingStory story = new BranchingStoryTreeParser().parse(new BufferedReader(new InputStreamReader(ActionTesting.class.getResourceAsStream("/utybo/branchingstorytree/api/test/bench/jse/" + path))), d, client);
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
