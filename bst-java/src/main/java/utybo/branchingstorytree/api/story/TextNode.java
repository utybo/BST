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
