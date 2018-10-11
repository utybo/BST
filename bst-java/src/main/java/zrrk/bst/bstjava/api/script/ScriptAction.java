/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script;

import org.atteo.classindex.IndexSubclasses;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.Experimental;
import zrrk.bst.bstjava.api.story.BranchingStory;

/**
 * A BST Action. A ScriptAction can cover more than one action (see
 * {@link #getName()}
 *
 * @author utybo
 *
 */
@IndexSubclasses
public interface ScriptAction
{
    /**
     * Execute this action
     *
     * @param head
     *            The name of the action, which is always one of the names from
     *            {@link #getName()}
     * @param desc
     *            The raw arguments to be used
     * @param line
     *            The line from which this action is from - this argument should
     *            be passed if a {@link BSTException} is thrown
     * @param story
     *            The story this action is executed from
     * @param client
     *            The client this action is executed from
     * @throws BSTException
     *             If something wrong happens : incorrect syntax, processing
     *             error... Make sure to pass the line argument to any thrown
     *             exception!
     */
    public void exec(String head, String desc, int line, BranchingStory story, BSTClient client)
            throws BSTException;

    /**
     * Gets the different names this ScriptAction represents.
     *
     * @return an array of all the names of the actions this implementation
     *         covers
     */
    public String[] getName();
    
    public default boolean isExperimental()
    {
        return this.getClass().isAnnotationPresent(Experimental.class);
    }
}
