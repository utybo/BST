/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

/**
 * A node that holds text.
 *
 * @author utybo
 *
 */
public class VirtualNode extends StoryNode
{
    private String text;

    /**
     * Create a virtual node with the given ID
     *
     * @param id
     *            the ID of the node to create
     */
    public VirtualNode(final int id, BranchingStory story)
    {
        super(id, story);
    }

    /**
     * @return the text contained in this Virtual node
     */
    public String getText()
    {
        return text;
    }

    /**
     * Set this node's text
     *
     * @param text
     *            this node's new text
     */
    public void setText(final String text)
    {
        this.text = text;
    }

    /**
     * Add a string at the end of this node's text
     *
     * @param toAppend
     *            the string to append
     */
    public void appendText(final String toAppend)
    {
        text += toAppend;
    }

}
