package utybo.branchingstorytree.api.story;

import java.util.ArrayList;
import java.util.List;

public class TextNode extends StoryNode
{
    public TextNode(int id)
    {
        super(id);
    }

    private List<NodeOption> options = new ArrayList<>();
    private String text;

    public List<NodeOption> getOptions()
    {
        return options;
    }

    public void setOptions(List<NodeOption> options)
    {
        this.options = options;
    }

    public void addOption(NodeOption option)
    {
        options.add(option);
    }

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
