/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.xbf;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.script.ExtNNDFactory;
import utybo.branchingstorytree.api.script.NextNodeDefiner;

public class XBFNextNodeDefinerFactory implements ExtNNDFactory
{

    @Override
    public NextNodeDefiner createNND(String head, String desc, BSTClient client)
    {
        return new XBFNextNodeDefiner(head, desc, client);
    }

    @Override
    public String[] getNames()
    {
        return new String[] {"xbf"};
    }

}
