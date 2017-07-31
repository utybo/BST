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
 * The equivalent of an {@link ActionDescriptor} but for checkers.
 *
 * @author utybo
 *
 */
public class CheckerDescriptor
{
    /**
     * The checker represented by this {@link CheckerDescriptor}
     */
    private final ScriptChecker checker;

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
     * The Client from which this {@link CheckerDescriptor} is created
     */
    private final BSTClient client;

    /**
     * The Branching story from which this {@link CheckerDescriptor} is created
     */
    private final BranchingStory story;

    /**
     * The line in the original script, or -1 if not applicable
     */
    private final int debugLine;

    /**
     * Creates a {@link CheckerDescriptor} using the given arguments.
     *
     * @param checker
     *            The action described by this descriptor
     * @param head
     *            The head of the script (the name of the checker)
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
    public CheckerDescriptor(final ScriptChecker checker, final String head, final String desc, final int debugLine, final BranchingStory story, final BSTClient client) throws BSTException
    {
        if(checker == null)
        {
            throw new BSTException(-1, "Checker " + head + " does not exist", story);
        }
        this.checker = checker;
        this.head = head;
        this.desc = desc;
        this.client = client;
        this.story = story;
        this.debugLine = debugLine;
    }

    /**
     * Executes the underlying checker, returning a boolean from the checker's
     * output
     *
     * @throws BSTException
     *             If the underlying checker throws a BSTException
     * @return the result of the checker
     */
    public boolean check() throws BSTException
    {
        return checker.check(head, desc, debugLine, story, client);
    }

    /**
     * @return The checker for this descriptor
     */
    public ScriptChecker getChecker()
    {
        return checker;
    }

    /**
     * @return The name of the checker
     */
    public String getHead()
    {
        return head;
    }

    /**
     * @return The raw, untouched arguments for the checker
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
