/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import utybo.branchingstorytree.brm.BRMHandler;
import utybo.branchingstorytree.ssb.SSBHandler;
import utybo.branchingstorytree.uib.UIBarHandler;

public interface BSTClient
{
    public String askInput(String message);

    public void exit();

    public default UIBarHandler getUIBarHandler()
    {
        return null;
    }
    
    public default SSBHandler getSSBHandler()
    {
        return null;
    }
    
    public default BRMHandler getBRMHandler()
    {
        return null;
    }
}
