/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.bdf;

import java.util.ArrayList;

import zrrk.bst.bstjava.api.script.VariableRegistry;

/**
 * Implementation of a BDF file's internal structure
 *
 * @author utybo
 *
 */
public class BDFFile
{
    private final ArrayList<BDFNode> nodes = new ArrayList<>();

    public BDFNode addNode(final BDFNode node)
    {
        nodes.add(node);
        return node;
    }

    public void applyTo(final VariableRegistry registry, final String prefix)
    {
        for(final BDFNode node : nodes)
        {
            node.applyTo(registry, prefix);
        }
    }
}
