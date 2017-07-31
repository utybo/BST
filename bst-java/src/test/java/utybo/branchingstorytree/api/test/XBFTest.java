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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.brm.BRMHandler;
import utybo.branchingstorytree.xbf.XBFHandler;

public class XBFTest
{
    private static Dictionnary dict;

    private static BranchingStoryTreeParser parser = new BranchingStoryTreeParser();

    private static BranchingStory mainStory;

    private class XBFTestClient implements BSTClient, BRMHandler, XBFHandler
    {
        private final HashMap<String, BranchingStory> stories = new HashMap<>();

        @Override
        public void load(InputStream in, String name) throws BSTException
        {
            try
            {
                BranchingStory bs = parser.parse(new BufferedReader(new InputStreamReader(in, "UTF-8")), dict, this, name, mainStory.getRegistry());
                stories.put(name, bs);
            }
            catch(Exception e)
            {
                throw new BSTException(-1, "Unexpected exception", e, name);
            }
        }

        @Override
        public BranchingStory getAdditionalStory(String name)
        {
            return stories.get(name);
        }

        @Override
        public void loadAuto() throws BSTException
        {
            load(getClass().getResourceAsStream("/utybo/branchingstorytree/api/test/xbf/resources/xbf/additional.bst"), "additional");
        }

        @Override
        public String askInput(String message)
        {
            return null;
        }

        @Override
        public void exit()
        {}

        @Override
        public BRMHandler getBRMHandler()
        {
            return this;
        }

        @Override
        public XBFHandler getXBFHandler()
        {
            return this;
        }

        @Override
        public BranchingStory getMainStory()
        {
            return mainStory;
        }

        @Override
        public Collection<String> getAdditionalStoryNames()
        {
            return Collections.unmodifiableCollection(stories.keySet());
        }

    }

    @Test
    public void testXBF() throws Exception
    {
        dict = new Dictionnary();
        XBFTestClient client = new XBFTestClient();
        mainStory = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/utybo/branchingstorytree/api/test/xbf/test.xbf"))), dict, client, "test");
        client.loadAuto();
        StoryNode sn = ((LogicalNode)mainStory.getInitialNode()).solve(mainStory);
        System.out.println(mainStory.getRegistry().dump());
        assert (int)mainStory.getRegistry().get("a", 0) == 12;
        assert sn instanceof TextNode && ((TextNode)sn).getText().equals("Hello world");
    }
}
