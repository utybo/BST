/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

import java.util.Collection;
import java.util.TreeMap;

import utybo.branchingstorytree.api.script.VariableRegistry;

public class BranchingStory extends TagHolder
{
    private int initialNode = 1;
    private final TreeMap<Integer, StoryNode> nodes = new TreeMap<>();
    private VariableRegistry registry = new VariableRegistry();

    public StoryNode getInitialNode()
    {
        if(getTag("initialnode") != null)
        {
            initialNode = Integer.parseInt(getTag("initialnode"));
        }
        return nodes.get(initialNode);
    }

    public void setInitialNode(final int initialNode)
    {
        this.initialNode = initialNode;
    }

    public StoryNode getNode(final int nodeId)
    {
        return nodes.get(nodeId);
    }

    public void addNode(final StoryNode node)
    {
        if(nodes.put(node.getId(), node) != null)
        {
            throw new IllegalArgumentException("A node already exists with this ID : " + node.getId());
        }
    }

    public int nextAvailableId()
    {
        return nodes.size();
    }

    public VariableRegistry getRegistry()
    {
        return registry;
    }
    
    public void setRegistry(VariableRegistry registry)
    {
        this.registry = registry;
    }

    public Collection<StoryNode> getAllNodes()
    {
        return nodes.values();
    }

    public void reset()
    {
        registry.reset();
    }

}
