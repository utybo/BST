/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTException;

public class IfNextNodeDefiner implements NextNodeDefiner
{
    private final int one, two;
    private final CheckerDescriptor checker;

    public IfNextNodeDefiner(final int one, final int two, final CheckerDescriptor checker)
    {
        this.one = one;
        this.two = two;
        this.checker = checker;
    }

    @Override
    public int getNextNode() throws BSTException
    {
        return checker.check() ? one : two;
    }

}
