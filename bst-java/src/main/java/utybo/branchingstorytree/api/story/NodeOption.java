/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

import java.util.ArrayList;
import java.util.List;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.script.AlwaysTrueChecker;
import utybo.branchingstorytree.api.script.CheckerDescriptor;
import utybo.branchingstorytree.api.script.NextNodeDefiner;

public class NodeOption extends TagHolder
{
    private NextNodeDefiner nextNode;
    private String text;
    private CheckerDescriptor checker;
    private final List<ActionDescriptor> doOnClick = new ArrayList<>();

    public NodeOption(final String text)
    {
        super();
        this.text = text;
    }

    public int getNextNode() throws BSTException
    {
        return nextNode.getNextNode();
    }

    public void setNextNode(final NextNodeDefiner nextNode)
    {
        this.nextNode = nextNode;
    }

    public String getText()
    {
        return text;
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    public CheckerDescriptor getChecker()
    {
        try
        {
            return checker == null ? new CheckerDescriptor(new AlwaysTrueChecker(), null, null, -1, null, null) : checker;
        }
        catch(BSTException e)
        {
            throw new Error("Impossible state", e);
        }
    }

    public void setChecker(final CheckerDescriptor checker)
    {
        this.checker = checker;
    }

    public List<ActionDescriptor> getDoOnClickActions()
    {
        return doOnClick;
    }

    public void addDoOnClick(final ActionDescriptor doOnClick)
    {
        this.doOnClick.add(doOnClick);
    }

}
