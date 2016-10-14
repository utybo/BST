package utybo.branchingstorytree.api.script;

public class StaticNextNode implements NextNodeDefiner
{
    private int nextNode;
    
    public StaticNextNode(int nextNode)
    {
        this.nextNode = nextNode;
    }

    @Override
    public int getNextNode()
    {
        return nextNode;
    }
    
}
