package utybo.branchingstorytree.api.story;

public class VirtualNode extends StoryNode
{

    public VirtualNode(int id)
    {
        super(id);
    }

    private String text;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void appendText(String toAppend)
    {
        this.text += toAppend;
    }

}
