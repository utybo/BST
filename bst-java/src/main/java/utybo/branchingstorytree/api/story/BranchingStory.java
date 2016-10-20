package utybo.branchingstorytree.api.story;

import java.util.Collection;
import java.util.TreeMap;

import utybo.branchingstorytree.api.script.VariableRegistry;

public class BranchingStory extends TagHolder
{
    private int initialNode = 1;
    private TreeMap<Integer, StoryNode> nodes = new TreeMap<>();
    private VariableRegistry registry = new VariableRegistry();

    public StoryNode getInitialNode()
    {
        if(getTag("initialnode") != null)
        {
            initialNode = Integer.parseInt(getTag("initialnode"));
        }
        return nodes.get(initialNode);
    }

    public void setInitialNode(int initialNode)
    {
        this.initialNode = initialNode;
    }

    public StoryNode getNode(int nodeId)
    {
        return nodes.get(nodeId);
    }

    public void addNode(StoryNode node)
    {
        if(nodes.put(node.getId(), node) != null)
            throw new IllegalArgumentException("A node already exists with this ID : " + node.getId());
    }

    public int nextAvailableId()
    {
        return nodes.size();
    }

    public VariableRegistry getRegistry()
    {
        return registry;
    }

    public Collection<StoryNode> getAllNodes()
    {
        return nodes.values();
    }

    public void reset()
    {
        registry.reset();
    }

}
