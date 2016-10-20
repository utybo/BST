package utybo.branchingstorytree.api.story;

import java.util.ArrayList;
import java.util.List;

public class TextNode extends VirtualNode
{
    public TextNode(int id)
    {
        super(id);
    }

    private List<NodeOption> options = new ArrayList<>();

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

}
