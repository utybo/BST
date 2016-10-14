package utybo.branchingstorytree.api.story;

public class StoryNode extends TagHolder
{

    private final int id;

    public int getId()
    {
        return id;
    }

    public StoryNode(int id)
    {
        this.id = id;
    }
}
