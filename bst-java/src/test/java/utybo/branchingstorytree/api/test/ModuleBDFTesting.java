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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.junit.Test;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.bdf.BDFFile;
import utybo.branchingstorytree.bdf.BDFHandler;
import utybo.branchingstorytree.bdf.BDFParser;
import utybo.branchingstorytree.brm.BRMHandler;

public class ModuleBDFTesting
{
    private class BDFClient implements BSTClient, BDFHandler, BRMHandler
    {
        private final HashMap<String, BDFFile> bdfFiles = new HashMap<>();

        @Override
        public void loadAuto() throws BSTException
        {
            load("/utybo/branchingstorytree/api/test/files/resources/bdf.bdf", "bdf");
        }

        @Override
        public void load(InputStream in, String name) throws BSTException
        {
            // Useless
        }

        @Override
        public void load(final String pathToResource, final String name) throws BSTException
        {
            try
            {
                bdfFiles.put(name, BDFParser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(pathToResource))), name));
            }
            catch(final IOException e)
            {
                throw new BSTException(-1, "Unexpected I/O error on BDF load", e, "<none>");
            }
        }

        @Override
        public BDFFile getBDFFile(final String name)
        {
            return bdfFiles.get(name);
        }

        @Override
        public String askInput(final String message)
        {
            // Useless
            return null;
        }

        @Override
        public void exit()
        {
            // Useless
        }

        @Override
        public BRMHandler getBRMHandler()
        {
            return this;
        }

        @Override
        public BDFHandler getBDFHandler()
        {
            return this;
        }
    }

    @Test
    public void testBDF() throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("module_bdf.bst", new BDFClient());
    }

    public static void testFile(final String path, final BSTClient client) throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        final Dictionnary d = new Dictionnary();
        final BranchingStory story = new BranchingStoryTreeParser().parse(new BufferedReader(new InputStreamReader(ActionTesting.class.getResourceAsStream("/utybo/branchingstorytree/api/test/files/" + path))), d, client, path);
        StoryNode node = story.getInitialNode();
        while(node != null)
        {
            if(node instanceof LogicalNode)
            {
                node = ((LogicalNode)node).solve(story);
            }
            else
            {
                throw new BSTException(-1, node.getId() + " isn't a logical node");
            }
        }
    }
}
