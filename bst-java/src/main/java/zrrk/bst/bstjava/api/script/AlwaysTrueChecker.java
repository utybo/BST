/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.BranchingStory;

/**
 * A {@link ScriptChecker} that always returns true
 *
 * @author utybo
 *
 */
public class AlwaysTrueChecker implements ScriptChecker
{
    @Override
    public boolean check(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        return true;
    }

    @Override
    public String[] getName()
    {
        return null;
    }
}
