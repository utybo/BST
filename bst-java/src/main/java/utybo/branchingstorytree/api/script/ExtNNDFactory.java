/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import org.atteo.classindex.IndexSubclasses;

import utybo.branchingstorytree.api.BSTClient;

@IndexSubclasses
public interface ExtNNDFactory
{
    public NextNodeDefiner createNND(String head, String desc, int line, BSTClient client);

    public String[] getNames();
}
