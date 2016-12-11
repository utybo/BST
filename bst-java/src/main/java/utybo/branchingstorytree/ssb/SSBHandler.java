/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.ssb;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface SSBHandler extends BRMResourceConsumer
{
    public void load(String relativePath, String name) throws BSTException;

    public void play(String name);

    public void ambient(String name);

    public void stop();
}
