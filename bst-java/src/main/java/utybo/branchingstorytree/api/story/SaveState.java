package utybo.branchingstorytree.api.story;

import utybo.branchingstorytree.api.script.VariableRegistry;

public class SaveState
{
    private int node;
    private VariableRegistry registry;

    public SaveState(int node, VariableRegistry vr)
    {
        this.node = node;
        registry = vr.clone();
    }

    public int getNode()
    {
        return node;
    }

    public void applySaveState(BranchingStory bs)
    {
        bs.setRegistry(registry);
    }
}
