/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

import utybo.branchingstorytree.api.script.VariableRegistry;

/**
 * An implementation of a save state mechanism -- do note this is rather
 * incomplete and you should beware of applying save states to modules too
 *
 * @author utybo
 *
 */
public class SaveState
{
    private final int nodeId;
    private final VariableRegistry registry;
    private String from;

    /**
     * Create a save state that will contain the given node id and a clone of
     * the given variable registry
     *
     * @param nodeId
     *            the node ID to be saved in the save state as the "current"
     *            node id
     * @param vr
     *            the variable registry to be cloned and saved in this save
     *            state.
     */
    public SaveState(final int nodeId, final VariableRegistry vr, final String from)
    {
        this.nodeId = nodeId;
        registry = vr.clone();
        this.from = from;
    }

    /**
     * @return the Node ID of this save state
     */
    public int getNodeId()
    {
        return nodeId;
    }

    /**
     * Apply the registry saved in this save state to the given story. You will
     * still need to do separate actions for some modules
     *
     * @param bs
     *            the branching story to apply the save state on.
     */
    public void applySaveState(final BranchingStory bs)
    {
        bs.setRegistry(registry);
    }

    public String getFrom()
    {
        return from;
    }
}
