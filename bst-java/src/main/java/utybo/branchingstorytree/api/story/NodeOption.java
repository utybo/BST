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

/**
 * An option of a {@link TextNode}
 * 
 * @author utybo
 *
 */
public class NodeOption extends TagHolder
{
    private NextNodeDefiner nextNode;
    private String text;
    private CheckerDescriptor checker;
    private final List<ActionDescriptor> doOnClick = new ArrayList<>();

    /**
     * Create an option with the given text
     * 
     * @param text
     *            the text of the option
     */
    public NodeOption(final String text)
    {
        super();
        this.text = text;
    }

    /**
     * Solve this option's next node definer
     * 
     * @return the next node
     * @throws BSTException
     *             if an exception occurs
     */
    public int getNextNode() throws BSTException
    {
        return nextNode.getNextNode();
    }

    /**
     * Set the next node definer for this option
     * 
     * @param nextNode
     */
    public void setNextNode(final NextNodeDefiner nextNode)
    {
        this.nextNode = nextNode;
    }

    /**
     * @return the text of this option
     */
    public String getText()
    {
        return text;
    }

    /**
     * Set this option's text
     * 
     * @param text
     *            the new option text
     */
    public void setText(final String text)
    {
        this.text = text;
    }

    /**
     * Get the checker descriptor that represents the checker determining the
     * availability of this option
     * 
     * @return just read what above this
     */
    public CheckerDescriptor getChecker()
    {
        try
        {
            return checker == null ? new CheckerDescriptor(new AlwaysTrueChecker(), null, null, -1, null, null) : checker;
        }
        catch(final BSTException e)
        {
            throw new Error("Impossible state", e);
        }
    }

    /**
     * Define the checker to be used to determine the availability of this
     * option
     * 
     * @param checker
     *            the new checker to be used blah blah
     */
    public void setChecker(final CheckerDescriptor checker)
    {
        this.checker = checker;
    }

    /**
     * Get the list of {@link ActionDescriptor} to be executed in order when
     * this option is chosen
     * 
     * @return
     */
    public List<ActionDescriptor> getDoOnClickActions()
    {
        return doOnClick;
    }

    /**
     * Add an option to be executed when this option is chosen
     * 
     * @param doOnClick
     */
    public void addDoOnClick(final ActionDescriptor doOnClick)
    {
        this.doOnClick.add(doOnClick);
    }

}
