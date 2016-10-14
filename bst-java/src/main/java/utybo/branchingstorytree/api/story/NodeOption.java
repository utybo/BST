package utybo.branchingstorytree.api.story;

import java.util.ArrayList;
import java.util.List;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.AlwaysTrueChecker;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.ScriptChecker;

public class NodeOption extends TagHolder
{
    private NextNodeDefiner nextNode;
    private String text;
    private ScriptChecker checker;
    private List<ScriptAction> doOnClick = new ArrayList<ScriptAction>();

    public NodeOption(String text)
    {
        super();
        this.text = text;
    }

    public int getNextNode() throws BSTException
    {
        return nextNode.getNextNode();
    }

    public void setNextNode(NextNodeDefiner nextNode)
    {
        this.nextNode = nextNode;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public ScriptChecker getChecker()
    {
        return checker == null ? new AlwaysTrueChecker() : checker;
    }

    public void setChecker(ScriptChecker checker)
    {
        this.checker = checker;
    }

    public List<ScriptAction> getDoOnClickActions()
    {
        return doOnClick;
    }

    public void addDoOnClick(ScriptAction doOnClick)
    {
        this.doOnClick.add(doOnClick);
    }

}
