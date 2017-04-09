/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.virtualfiles;

import java.util.ArrayList;
import java.util.Collection;

public class VirtualFileHolder extends ArrayList<VirtualFile>
{
    private static final long serialVersionUID = 1L;

    public VirtualFileHolder()
    {
        super();
    }

    public VirtualFileHolder(Collection<? extends VirtualFile> c)
    {
        super(c);
    }

    public VirtualFileHolder(int initialCapacity)
    {
        super(initialCapacity);
    }

    public VirtualFile getFile(String string)
    {
        for(VirtualFile vf : this)
        {
            if(vf.getName().equals(string))
                return vf;
        }
        return null;
    }
}
