/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import org.atteo.classindex.IndexSubclasses;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * A BST Checker. A ScriptChecker can cover more than one checker (see
 * {@link #getName()}
 * 
 * @author utybo
 *
 */
@IndexSubclasses
public interface ScriptChecker
{
    /**
     * Execute the check
     * 
     * @param head
     *            The name of the check, which is always one of the names from
     *            {@link #getName()}
     * @param desc
     *            The raw arguments to be used
     * @param line
     *            The line from which this checker is from - this argument
     *            should be passed if a {@link BSTException} is thrown
     * @param story
     *            The story this checker is executed from
     * @param client
     *            The client this checker is executed from
     * @throws BSTException
     *             If something wrong happens : incorrect syntax, processing
     *             error... Make sure to pass the line argument to any thrown
     *             exception!
     */
    public boolean check(String head, String desc, int line, BranchingStory story, BSTClient client) throws BSTException;

    /**
     * Gets the different names this ScriptChecker represents.
     * 
     * @return an array of all the names of the checkers this implementation
     *         covers
     */
    public String[] getName();
}
