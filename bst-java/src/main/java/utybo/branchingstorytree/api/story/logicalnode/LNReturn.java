/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story.logicalnode;

public class LNReturn extends LNInstruction
{
    private final int next;

    public LNReturn(final int next)
    {
        this.next = next;
    }

    @Override
    public int execute()
    {
        return next;
    }

}