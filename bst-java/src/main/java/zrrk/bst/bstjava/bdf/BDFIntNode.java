/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.bdf;

import zrrk.bst.bstjava.api.script.VariableRegistry;

/**
 * A BDFNode that represents an integer value
 *
 * @author utybo
 *
 */
public class BDFIntNode extends BDFNode
{
    private final int value;

    public BDFIntNode(final String name, final int i)
    {
        super(name);
        value = i;
    }

    @Override
    public void applyTo(final VariableRegistry registry, final String prefix)
    {
        registry.put(prefix + getName(), value);
    }

}
