package utybo.branchingstorytree.api.story.logicalnode;

public class LNReturn extends LNInstruction
{
    private final int next;

    public LNReturn(final int next)
    {
        this.next = next;
    }

    @Override
    public int execute()
    {
        return next;
    }

}