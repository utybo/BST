package utybo.branchingstorytree.swing.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.visuals.StoryPanel;
import utybo.branchingstorytree.xbf.XBFHandler;

public class XBFClient implements XBFHandler
{
    private HashMap<String, BranchingStory> stories = new HashMap<>();
    private StoryPanel sp;
    private BSTClient client;

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
            bs = new BranchingStoryTreeParser().parse(new BufferedReader(new InputStreamReader(in, "UTF-8")), new Dictionnary(), client, name);
        }
        catch(InstantiationException | IllegalAccessException | IOException e)
        {
            throw new BSTException(-1, "Unexpected exception", e, name);
        }
        stories.put(name, bs);
        bind(sp.getStory(), bs, name);
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
}
