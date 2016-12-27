/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.img;

import utybo.branchingstorytree.brm.BRMResourceConsumer;

/**
 * Handler for the IMG module
 * 
 * @author utybo
 *
 */
public interface IMGHandler extends BRMResourceConsumer
{
    /**
     * Set the background to the resource with the given name
     * 
     * @param name
     */
    public void setBackground(String name);
}
