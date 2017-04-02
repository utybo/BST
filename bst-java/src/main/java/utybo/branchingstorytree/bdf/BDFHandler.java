/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.bdf;

import utybo.branchingstorytree.brm.BRMResourceConsumer;

/**
 * Handler for the BDF module
 *
 * @author utybo
 *
 */
public interface BDFHandler extends BRMResourceConsumer
{
    /**
     * Get an instance of BDFFile with the file name (being the name without the
     * extension) of name
     *
     * @param name
     * @return
     */
    public BDFFile getBDFFile(String name);
}
