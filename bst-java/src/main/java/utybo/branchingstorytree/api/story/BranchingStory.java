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

/**
 * A BST Story
 *
 * @author utybo
 *
 */
public class BranchingStory extends TagHolder
{
    /**
     * The initial node -- by default, 1
     */
    private int initialNode = 1;

    /**
     * The nodes contained in this story
     */
    private final TreeMap<Integer, StoryNode> nodes = new TreeMap<>();

    /**
     * The variables used in this story
     */
    private final VariableRegistry registry = new VariableRegistry();

    /**
     * @return The initial node of the story, which comes from the initialnode
     *         first-level tag or the default node, which is the value set by
     *         {@link #setInitialNode(int)} or 1 by default
     */
    public StoryNode getInitialNode()
    {
        if(getTag("initialnode") != null)
        {
            initialNode = Integer.parseInt(getTag("initialnode"));
        }
        return nodes.get(initialNode);
    }

    /**
     * Set the default initial node. This can be overriden by the tag
     * initialnode
     *
     * @param initialNode
     *            the new default initial node
     */
    public void setInitialNode(final int initialNode)
    {
        this.initialNode = initialNode;
    }

    /**
     * @param nodeId
     *            The ID of the node we're looking for
     * @return The node with the given ID, or null if there isn't any that
     *         matches the ID
     */
    public StoryNode getNode(final int nodeId)
    {
        return nodes.get(nodeId);
    }

    /**
     * Add a node to the story. There cannot be multiple nodes with the same ID
     *
     * @param node
     *            the node to add
     * @throws IllegalArgumentException
     *             if a node with the node to be added's ID already exists.
     */
    public void addNode(final StoryNode node)
    {
        if(nodes.put(node.getId(), node) != null)
        {
            throw new IllegalArgumentException("A node already exists with this ID : " + node.getId());
        }
    }

    /**
     *
     * @return an id that is likely to be empty.
     * @deprecated This method relies on the possibility that all the nodes have
     *             IDs that are continuous, which is almost never the case.
     *             Never use this method.
     */
    @Deprecated
    public int nextAvailableId()
    {
        return nodes.size();
    }

    public int nextAvailableAuto()
    {
        for(int i = -2;; i--)
        {
            if(!nodes.containsKey(i))
            {
                return i;
            }

        }
    }

    /**
     * @returns the registry used by this story
     */
    public VariableRegistry getRegistry()
    {
        return registry;
    }

    /**
     * Replace the current registry's content by the given one. This clears the
     * registry then merges the values of the given registry
     *
     * @param registry
     *            the new registry content to use.
     */
    public void setRegistry(final VariableRegistry registry)
    {
        this.registry.clear();
        this.registry.merge(registry);
    }

    /**
     * Get all the nodes from this story.
     *
     * @return a collection of all the existing nodes
     */
    public Collection<StoryNode> getAllNodes()
    {
        return nodes.values();
    }

    /**
     * Reset this story in a destructive way -- while this does not affect the
     * structure of the story, this clears the registry. Use with caution.
     */
    public void reset()
    {
        registry.reset();
    }

}
