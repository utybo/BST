/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.virtualfiles;

public class VirtualFile
{
    private final byte[] data;
    private final String name;

    public VirtualFile(byte[] data, String name)
    {
        super();
        this.data = data.clone();
        this.name = name;
    }

    public byte[] getData()
    {
        return data.clone();
    }

    public String getName()
    {
        return name;
    }

}
