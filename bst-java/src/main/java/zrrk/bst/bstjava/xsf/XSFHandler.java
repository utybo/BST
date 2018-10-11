/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.xsf;

import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.brm.BRMResourceConsumer;

public interface XSFHandler extends BRMResourceConsumer
{
    public Object invokeScript(String resourceName, String function, XSFBridge bst,
            BranchingStory story, int line) throws BSTException;

    public void createEngine(BranchingStory story, int line, int engine, String... toLoad) throws BSTException;

    public Object invokeScriptInEngine(int engine, String toEval, XSFBridge xsfBridge,
            BranchingStory story, int line) throws BSTException;

    public void importAllVariables(int i, BranchingStory story, int line) throws BSTException;
}
