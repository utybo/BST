/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.ImageIcon;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.BranchingStoryTreeParser;
import zrrk.bst.bstjava.api.script.Dictionary;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.xbf.XBFHandler;
import zrrk.bst.openbst.Icons;
import zrrk.bst.openbst.Messagers;
import zrrk.bst.openbst.OpenBST;
import zrrk.bst.openbst.utils.Lang;
import zrrk.bst.openbst.utils.LanguageUtils;
import zrrk.bst.openbst.visuals.StoryPanel;

public class XBFClient implements XBFHandler
{
    private final HashMap<String, BranchingStory> stories = new HashMap<>();
    private final StoryPanel sp;
    private final BSTClient client;

    public XBFClient(StoryPanel sp, BSTClient client)
    {
        this.sp = sp;
        this.client = client;
    }

    @Override
    public void load(InputStream in, String name) throws BSTException
    {
        BranchingStory bs;
        try
        {
            bs = new BranchingStoryTreeParser().parse(
                    new BufferedReader(new InputStreamReader(in, "UTF-8")), new Dictionary(),
                    client, name, sp.getStory().getRegistry());
            if(LanguageUtils.checkNonLatin(bs))
            {
                if((int)bs.getRegistry().get("__nonlatinwarned", 0) == 0)
                {
                    Messagers.showMessage(OpenBST.getGUIInstance(), Lang.get("story.unicodecompat"),
                            Messagers.TYPE_INFO, Lang.get("story.unicodecompat.title"),
                            new ImageIcon(Icons.getImage("LanguageError", 48)));
                }
                bs.getRegistry().put("__nonlatin_" + bs.getTag("__sourcename"), 1);
            }
        }
        catch(InstantiationException | IllegalAccessException | IOException e)
        {
            throw new BSTException(-1, "Unexpected exception", e, name);
        }
        stories.put(name, bs);
    }

    @Override
    public BranchingStory getAdditionalStory(String name)
    {
        return stories.get(name);
    }

    @Override
    public BranchingStory getMainStory()
    {
        return sp.getStory();
    }

    @Override
    public Collection<String> getAdditionalStoryNames()
    {
        return Collections.unmodifiableCollection(stories.keySet());
    }
}
