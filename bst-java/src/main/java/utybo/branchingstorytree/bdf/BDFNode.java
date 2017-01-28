/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.bdf;

import utybo.branchingstorytree.api.script.VariableRegistry;

/**
 * A node inside a {@link BDFFile}
 * 
 * @author utybo
 *
 */
public abstract class BDFNode
{
    private final String name;

    public BDFNode(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public abstract void applyTo(VariableRegistry registry, String prefix);
}
