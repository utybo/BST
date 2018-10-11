/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.editor;

import zrrk.bst.bstjava.api.BSTException;

public interface EditorControl<F>
{
    public void importFrom(F from) throws BSTException;

    public default F exportToObject() throws BSTException
    {
        return null;
    }

    public String exportToString() throws BSTException;
}
