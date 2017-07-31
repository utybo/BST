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

/**
 * A Text Node is a {@link VirtualNode} with options
 *
 * @author utybo
 *
 */
public class TextNode extends VirtualNode
{
    private List<NodeOption> options = new ArrayList<>();

    /**
     * Create a Text Node with the given ID
     *
     * @param id
     *            the ID of this text node
     */
    public TextNode(final int id, BranchingStory story)
    {
        super(id, story);
    }

    /**
     * @return all the options from this node
     */
    public List<NodeOption> getOptions()
    {
        return options;
    }

    /**
     * Set the options of this text node
     *
     * @param options
     *            the options
     */
    public void setOptions(final List<NodeOption> options)
    {
        this.options = options;
    }

    /**
     * Add an option to this text node.
     *
     * @param option
     *            the option to add
     */
    public void addOption(final NodeOption option)
    {
        options.add(option);
    }

}
