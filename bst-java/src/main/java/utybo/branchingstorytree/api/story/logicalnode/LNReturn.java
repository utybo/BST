package utybo.branchingstorytree.api.story.logicalnode;

public class LNReturn extends LNInstruction
{
    private int next;

    public LNReturn(int next)
    {
        this.next = next;
    }

    @Override
    public int execute()
    {
        return next;
    }

}