/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;

/**
 * A BRM handler with more abstraction required by OpenBST
 * 
 * @author utybo
 *
 */
public interface BRMAdvancedHandler extends BRMHandler
{
    public void restoreSaveState() throws BSTException;
    
    /**
     * A load method to replace the action-triggered from BRMHandler
     * @throws BSTException
     */
    public void load() throws BSTException;
}
