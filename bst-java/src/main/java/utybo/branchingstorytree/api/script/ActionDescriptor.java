/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * ActionDescriptor acts as a bridge between higher-level elements and
 * {@link ScriptAction}. It makes execution of actions fairly easy, as it
 * provides a handy wrapper around them that holds all the required values
 *
 * @author utybo
 *
 */
public class ActionDescriptor
{
    /**
     * The action represented by this {@link ActionDescriptor}
     */
    private final ScriptAction action;

    /**
     * The "head" of the original script. This is the name of the action to be
     * performed.
     */
    private final String head;

    /**
     * The "description" (or "tail") from the original script. This contains all
     * the arguments that are left untouched
     */
    private final String desc;

    /**
     * The Client from which this {@link ActionDescriptor} is created
     */
    private final BSTClient client;

    /**
     * The Branching story from which this {@link ActionDescriptor} is created
     */
    private final BranchingStory story;

    /**
     * The line in the original script, or -1 if not applicable
     */
    private final int debugLine;

    /**
     * Creates an {@link ActionDescriptor} using the given arguments.
     *
     * @param action
     *            The action described by this descriptor
     * @param head
     *            The head of the script (the name of the action)
     * @param desc
     *            The description or tail of the script (the raw arguments)
     * @param debugLine
     *            The line from which this descriptor is parsed (or -1 if N/A)
     * @param story
     *            The story this descriptor is created from
     * @param client
     *            The client linked to this descriptor
     * @throws BSTException
     *             If the action is null
     */
    public ActionDescriptor(final ScriptAction action, final String head, final String desc, final int debugLine, final BranchingStory story, final BSTClient client) throws BSTException
    {
        if(action == null)
        {
            throw new BSTException(debugLine, "Action " + head + " does not exist", story.getTag("__sourcename"));
        }
        this.action = action;
        this.head = head;
        this.desc = desc;
        this.client = client;
        this.story = story;
        this.debugLine = debugLine;
    }

    /**
     * Executes the wrapped action, passing all the arguments required stored in
     * this descriptor
     *
     * @throws BSTException
     *             If the underlying action throws a BSTException
     */
    public void exec() throws BSTException
    {
        action.exec(head, desc, debugLine, story, client);
    }

    /**
     * @return The action this descriptor is wrapped around
     */
    public ScriptAction getAction()
    {
        return action;
    }

    /**
     * @return The name of the action
     */
    public String getHead()
    {
        return head;
    }

    /**
     * @return The raw, untouched arguments for the action
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * @return The client the descriptor is from
     */
    public BSTClient getClient()
    {
        return client;
    }

    /**
     * @return The story this descriptor is from
     */
    public BranchingStory getStory()
    {
        return story;
    }

    /**
     * @return The line in the original file, or -1 if N/A
     */
    public int getLine()
    {
        return debugLine;
    }
}
