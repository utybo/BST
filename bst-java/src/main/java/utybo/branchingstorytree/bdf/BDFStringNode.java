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
 * A {@link BDFFile} node that represetns a string value
 *
 * @author utybo
 *
 */
public class BDFStringNode extends BDFNode
{
    private String value;

    public BDFStringNode(final String name, final String value)
    {
        super(name);
        this.value = value;
    }

    @Override
    public void applyTo(final VariableRegistry registry, final String prefix)
    {
        registry.put(prefix + getName(), value);
    }

    public void append(final String s)
    {
        value += s;
    }
}
